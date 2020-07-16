package com.lhq.superboot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhq.superboot.domain.OrderNotifyRtn;
import com.lhq.superboot.domain.OrderPayRtn;
import com.lhq.superboot.domain.PayParam;
import com.lhq.superboot.domain.WechatPayNotifyDto;
import com.lhq.superboot.domain.WechatPayParam;
import com.lhq.superboot.domain.WechatUnhandleDto;
import com.lhq.superboot.enums.NotifyCode;
import com.lhq.superboot.enums.OrderPayType;
import com.lhq.superboot.enums.PayType;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.PayLogService;
import com.lhq.superboot.service.WxOrderPayService;
import com.lhq.superboot.util.BigDecimalUtils;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.IdSequenceUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.wechat.WechatPayApi;
import com.lhq.superboot.wechat.util.WechatPayConstants;

/**
 * @Description: 微信下单支付
 * @author: lihaoqi
 * @date: 2019年9月4日 下午5:35:04
 * @version: v1.0.0
 */
@Service
public class WxOrderPayServiceImpl implements WxOrderPayService {

    private static final Logger logger = LoggerFactory.getLogger(WxOrderPayService.class);

    /**
     * 支付类型
     **/
    private static final String PAY_TYPE = PayType.ORDER_PAY.getCode();
    /**
     * 微信下单充值固定参数
     **/
    private static final String BODY_PARAM = "哈达通-用户下单支付";

    /**
     * redis下单订单号的key前缀
     **/
    private static final String ORDER_KEY = "lhq:superboot:user:recharge:ordermsg:";
    /**
     * redis下单商品订单号的对应的冻结token数
     **/
    private static final String ORDER_TOKRN_KEY = "lhq:superboot:user:order:ordertoken:";
    /**
     * 随机生成订单号前缀：user-order简称
     **/
    private static final String ORDER_PRE = "UO";
    /**
     * 下单订单存在redis时间 2天
     **/
    private static final Long ORDER_EXPIRE = 60 * 60 * 24 * 2L;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private WechatPayApi wechatPayApi;

    //@Autowired
    //private UserTokenService userTokenService;

    @Autowired
    private PayLogService payLogService;

    @Autowired
    private IdSequenceUtils idSequenceUtils;

    @Override
    @Transactional
    public OrderPayRtn order(PayParam payParam) {
        logger.debug("[WxOrderPayService] -> 商品下单预支付");
        OrderPayRtn.OrderPayRtnBuilder orderPayRtn = new OrderPayRtn().toBuilder();
        long money = BigDecimalUtils.toLong(payParam.getMoney());
        long token = BigDecimalUtils.toLong(payParam.getToken());
        String userId = payParam.getUserId();
        String openId = payParam.getWechatId();
        String orderNo = payParam.getOrderNo();
        String outTradeNo = ORDER_PRE + idSequenceUtils.nextId();

        // 判断下单支付类型
        if (money == 0) {
            // 全token时、直接扣减token值返回即可
            // 修改用户token数据 && 记录token的流水
            // String payTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // userTokenService.updateUserToken(userId, token, IS_INCOME.NO.value(), PAY_TYPE, payTime, IS_FREEZE_TOKEN.NO.value());

            orderPayRtn.orderPayType(OrderPayType.ALL_TOKEN.getCode());
        } else if (token == 0) {
            // 全金额时、直接调用微信支付返回预下单参数
            redisUtils.set(ORDER_KEY + outTradeNo, FastJsonUtils.toJSONFeatures(payParam), ORDER_EXPIRE);
            // 调用微信下单
            WechatPayParam wxPayParam = new WechatPayParam().toBuilder().body(BODY_PARAM).payType(PAY_TYPE).money(money + "")
                    .notifyUrl(WechatPayConstants.GOODS_PAY_NOTIFY_URL).openid(openId).userId(userId).orderNo(orderNo).outTradeNo(outTradeNo).build();
            String wxPreOrderStr = wechatPayApi.unifiedOrder(wxPayParam);
            if (StringUtils.isEmpty(wxPreOrderStr)) {
                logger.error("[WxOrderPayService] -> 微信下单失败，无法获取预下单参数");
                throw new SuperBootException("lhq-superboot-wechat-0008");
            }
            orderPayRtn.orderPayType(OrderPayType.ALL_MONEY.getCode()).wxPreOrderStr(wxPreOrderStr);
        } else {
            // 半token半金额、冻结需要支付的token、调用微信支付返回预下单参数
            redisUtils.set(ORDER_KEY + outTradeNo, FastJsonUtils.toJSONFeatures(payParam), ORDER_EXPIRE);
            // 调用微信下单
            WechatPayParam wxPayParam = new WechatPayParam().toBuilder().body(BODY_PARAM).payType(PAY_TYPE).money(money + "")
                    .notifyUrl(WechatPayConstants.GOODS_PAY_NOTIFY_URL).openid(openId).userId(userId).orderNo(orderNo).outTradeNo(outTradeNo).build();
            String wxPreOrderStr = wechatPayApi.unifiedOrder(wxPayParam);
            if (StringUtils.isEmpty(wxPreOrderStr)) {
                logger.error("[WxOrderPayService] -> 微信下单失败，无法获取预下单参数");
                throw new SuperBootException("lhq-superboot-wechat-0008");
            }
            // 微信下单成功后、冻结下单的token值
            //userTokenService.freezeToken(userId, token);
            orderPayRtn.orderPayType(OrderPayType.HALF_TOKEN_MONEY.getCode()).wxPreOrderStr(wxPreOrderStr);
        }

        return orderPayRtn.build();
    }

    @Override
    @Transactional
    public OrderNotifyRtn orderNotify() {
        logger.debug("[WxOrderPayService] -> 用户下单支付通知");
        try {
            WechatPayNotifyDto wechatPayNotifyDto = wechatPayApi.wechatPayNotify();
            Integer notifyCode = wechatPayNotifyDto.getCode();
            String notifyResult = wechatPayNotifyDto.getResult();
            String orderNo = wechatPayNotifyDto.getOrderNo();
            String outTradeNo = wechatPayNotifyDto.getOutTradeNo();

            // 获取缓存中的信息
            PayParam payParam = getOrderMsg(outTradeNo);
            Long money = BigDecimalUtils.toLong(payParam.getMoney());
            Long token = BigDecimalUtils.toLong(payParam.getToken());
            String userId = payParam.getUserId();

            logger.debug("[WxOrderPayService] -> 用户下单支付通知描述：{}", wechatPayNotifyDto.getDesc());
            // 通知且第一次通知成功时，记录token流水、修改用户token值
            if (NotifyCode.SUCCESS.getCode() == notifyCode) {
                // 支付成功，解冻token并扣减token值、并记录金额支出
                String payTime = wechatPayNotifyDto.getPayTime();
                String bankType = wechatPayNotifyDto.getBankType();
                long totalFee = wechatPayNotifyDto.getMoney();

                // 校验金额
                if (money != totalFee) {
                    logger.error("[WxOrderPayService] -> 用户充值订单信息金额与支付金额不相符，订单号：{}", orderNo);
                    throw new SuperBootException("lhq-superboot-recharge-0005");
                }
                // 当花费了token时才扣减并解冻token
                //if (token > 0) {
                // userTokenService.updateUserToken(userId, token, IS_INCOME.NO.value(), PAY_TYPE, payTime, IS_FREEZE_TOKEN.YES.value());
                //}
                // 写入金额日志(支付成功记录)
                payLogService.insertPayLog(userId, orderNo, outTradeNo, PAY_TYPE, bankType, totalFee, payTime);
            } /*else {
                 支付失败，解冻token值
                if (token > 0) {
                     userTokenService.unfreezeToken(userId, token);
                }
            }*/
            return new OrderNotifyRtn().toBuilder().notifyCode(notifyCode).notifyResult(notifyResult).orderNo(orderNo).build();
        } catch (Exception e) {
            logger.debug("[WxOrderPayService] -> 用户充值支付通知报错，错误信息：{}", e.getMessage());
            throw new SuperBootException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealUnHandleOrder(String outTradeNo, String userId, String payType) throws Exception {
        logger.debug("[WxOrderPayService] -> 处理下单超时未处理的订单");

        // 获取微信订单信息
        WechatUnhandleDto wxUnhandleDto = wechatPayApi.dealUnHandleOrder(outTradeNo, userId);
        // 获取缓存中的订单信息
        PayParam payParam = getOrderMsg(outTradeNo);
        Long token = BigDecimalUtils.toLong(payParam.getToken());
        String orderNo = payParam.getOrderNo();

        if (wxUnhandleDto.isSuccess()) {
            String payTime = wxUnhandleDto.getPayTime();
            String bankType = wxUnhandleDto.getBankType();
            Long money = Long.parseLong(wxUnhandleDto.getCashFee());

            // 当花费了token时才扣减并解冻token
            //if (token > 0) {
            // userTokenService.updateUserToken(userId, token, IS_INCOME.NO.value(), PAY_TYPE, payTime, IS_FREEZE_TOKEN.YES.value());
            //}
            // 写入金额日志(支付成功记录)
            payLogService.insertPayLog(userId, orderNo, outTradeNo, payType, bankType, money, payTime);
        } /*else {
             支付失败，解冻token值
            if (token > 0) {
                 userTokenService.unfreezeToken(userId, token);
            }
        }*/
    }

    /**
     * @param outTradeNo
     * @return
     * @Description: 获取缓存中的下单订单信息
     */
    private PayParam getOrderMsg(String outTradeNo) {
        // 通过订单号找redis的订单信息，校验金额，获取token
        Object rechargeMsg = redisUtils.get(ORDER_KEY + outTradeNo);
        if (rechargeMsg == null) {
            logger.error("[WxOrderPayService] -> 用户下单订单信息丢失，微信交易订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-order-0003", outTradeNo);
        }
        PayParam payParam;
        try {
            payParam = FastJsonUtils.toBean((String) rechargeMsg, PayParam.class);
        } catch (Exception e) {
            logger.error("[WxOrderPayService] -> 用户下单订单信息参数异常，微信交易订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-order-0004", outTradeNo);
        }
        return payParam;
    }

}
