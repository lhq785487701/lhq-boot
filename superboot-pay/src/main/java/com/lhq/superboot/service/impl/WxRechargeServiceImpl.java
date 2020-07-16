package com.lhq.superboot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhq.superboot.domain.PayParam;
import com.lhq.superboot.domain.WechatPayNotifyDto;
import com.lhq.superboot.domain.WechatPayParam;
import com.lhq.superboot.domain.WechatUnhandleDto;
import com.lhq.superboot.enums.NotifyCode;
import com.lhq.superboot.enums.PayType;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.PayLogService;
import com.lhq.superboot.service.WxRechargeService;
import com.lhq.superboot.util.BigDecimalUtils;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.IdSequenceUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.vo.RechargeVo;
import com.lhq.superboot.wechat.WechatPayApi;
import com.lhq.superboot.wechat.util.WechatPayConstants;

/**
 * @Description 微信充值token实现类
 * @author: lihaoqi
 * @date: 2019年7月18日 下午4:03:35
 * @version: v1.0.0
 */
@Service
public class WxRechargeServiceImpl implements WxRechargeService {

    private static final Logger logger = LoggerFactory.getLogger(WxRechargeService.class);

    /**
     * 充值类型
     **/
    private static final String PAY_TYPE = PayType.RECHARGE.getCode();
    /**
     * 微信下单充值固定参数
     **/
    private static final String BODY_PARAM = "用户积分充值";
    /**
     * 随机生成订单号前缀：user-recharge简称
     **/
    private static final String ORDER_PRE = "UR";

    /**
     * redis记录充值订单号的key前缀
     **/
    private static final String RECHARGE_ORDER_KEY = "lhq:superboot:user:recharge:ordermsg:";
    /**
     * 充值订单存在redis时间 2天
     **/
    private static final Long RECHARGE_ORDER_EXPIRE = 60 * 60 * 24 * 2L;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private WechatPayApi wechatPayApi;

    @Autowired
    private PayLogService payLogService;

    @Autowired
    private IdSequenceUtils idSequenceUtils;


    @Override
    public String rechargeOrder(RechargeVo rechargeVo) {
        logger.debug("[WxRechargeService] -> 用户充值积分下单");
        // 创建充值唯一订单号(充值没有单独订单号，所以该订单号为交易号)
        String outTradeNo = ORDER_PRE + idSequenceUtils.nextId();
        String orderNo = outTradeNo;

        // 记录订单号以及相关信息，主要记录token数量(与支付时记录的订单信息区分开)
        rechargeVo.setOrderNo(orderNo);

        redisUtils.set(RECHARGE_ORDER_KEY + outTradeNo, FastJsonUtils.toJSONFeatures(rechargeVo), RECHARGE_ORDER_EXPIRE);

        // 调用微信下单传参
        WechatPayParam wxPayParam = new WechatPayParam().toBuilder().body(BODY_PARAM).payType(PAY_TYPE)
                .money(BigDecimalUtils.toLong(rechargeVo.getMoney()).toString()).notifyUrl(WechatPayConstants.RECHARGE_NOTIFY_URL)
                .openid(rechargeVo.getWechatId()).userId(rechargeVo.getUserId()).orderNo(orderNo).outTradeNo(outTradeNo).build();
        // 微信再次签名后将参数返回小程序，又小程序直接调用支付接口
        return wechatPayApi.unifiedOrder(wxPayParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rechargeNotify() {
        logger.debug("[WxRechargeService] -> 用户充值支付通知");
        try {
            WechatPayNotifyDto wechatPayNotifyDto = wechatPayApi.wechatPayNotify();
            logger.debug("[WxOrderPayService] -> 从微信模块获取用户通知返回对象：{}", wechatPayNotifyDto);
            Integer notifyCode = wechatPayNotifyDto.getCode();
            String notifyResult = wechatPayNotifyDto.getResult();
            logger.debug("[WxOrderPayService] -> 该订单已经处理完毕，处理结果:{}，原因:{}", notifyCode, notifyResult);

            // 如果处理完毕，则不需要再次通知，返回处理成功的信息给微信
            if (NotifyCode.HANDLED.getCode() == notifyCode) {
                return notifyResult;
            }

            logger.debug("[WxRechargeService] -> 用户充值支付通知描述：{}", wechatPayNotifyDto.getDesc());
            // 通知且第一次通知成功时，记录token流水、修改用户token值
            if (NotifyCode.SUCCESS.getCode() == notifyCode) {
                String payTime = wechatPayNotifyDto.getPayTime();
                String orderNo = wechatPayNotifyDto.getOrderNo();
                String outTradeNo = wechatPayNotifyDto.getOutTradeNo();
                String bankType = wechatPayNotifyDto.getBankType();
                long totalFee = wechatPayNotifyDto.getMoney();

                PayParam payParam = getRechargeMsg(outTradeNo);
                long money = BigDecimalUtils.toLong(payParam.getMoney());
                String userId = payParam.getUserId();
                // 校验金额
                if (money != totalFee) {
                    logger.error("[WxRechargeService] -> 用户充值订单信息金额与支付金额不相符，订单号：{}", orderNo);
                    throw new SuperBootException("lhq-superboot-recharge-0005");
                }
                // 写入金额日志(支付成功记录)
                payLogService.insertPayLog(userId, orderNo, outTradeNo, PAY_TYPE, bankType, totalFee, payTime);
            }
            return notifyResult;
        } catch (Exception e) {
            logger.debug("[WxRechargeService] -> 用户充值支付通知报错，错误信息：{}", e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealUnHandleRecharge(String outTradeNo, String userId, String payType) throws Exception {
        logger.debug("[WxRechargeService] -> 处理用户充值未处理的情况");

        WechatUnhandleDto wxUnhandleDto = wechatPayApi.dealUnHandleOrder(outTradeNo, userId);
        if (wxUnhandleDto.isSuccess()) {
            String payTime = wxUnhandleDto.getPayTime();
            String bankType = wxUnhandleDto.getBankType();
            PayParam payParam = getRechargeMsg(outTradeNo);
            long token = BigDecimalUtils.toLong(payParam.getToken());
            long money = Long.parseLong(wxUnhandleDto.getCashFee());
            String orderNo = payParam.getOrderNo();

            logger.debug("花费的积分{},尚未记录", token);
            // 写入金额日志(支付成功记录)
            payLogService.insertPayLog(userId, orderNo, outTradeNo, payType, bankType, money, payTime);
        }
    }

    /**
     * @param outTradeNo
     * @return
     * @Description: 获取缓存中的充值订单信息
     */
    private PayParam getRechargeMsg(String outTradeNo) {
        // 通过订单号找redis的订单信息，校验金额，获取token
        Object rechargeMsg = redisUtils.get(RECHARGE_ORDER_KEY + outTradeNo);
        if (rechargeMsg == null) {
            logger.error("[WxRechargeService] -> 用户充值订单信息丢失，交易订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-recharge-0003", outTradeNo);
        }
        PayParam payParam;
        try {
            payParam = FastJsonUtils.toBean((String) rechargeMsg, PayParam.class);
        } catch (Exception e) {
            logger.error("[WxRechargeService] -> 用户充值订单信息参数异常，交易订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-recharge-0004", outTradeNo);
        }
        return payParam;
    }

}
