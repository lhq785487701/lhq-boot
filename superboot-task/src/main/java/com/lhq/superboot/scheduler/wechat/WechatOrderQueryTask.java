package com.lhq.superboot.scheduler.wechat;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.lhq.superboot.service.WxOrderPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.WechatOrderCache;
import com.lhq.superboot.enums.ConstEnumUtils.IS_HANDLE;
import com.lhq.superboot.enums.PayType;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.WxPayProperty;
import com.lhq.superboot.service.RechargeService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.RedisUtils;

/**
 * @Description 微信订单检查通知
 * @author: lihaoqi
 * @date: 2019年7月11日 下午4:15:11
 * @version: v1.0.0
 */
@Service
public class WechatOrderQueryTask {

    private static final Logger logger = LoggerFactory.getLogger(WechatOrderQueryTask.class);

    /**
     * 超过多少时间未处理的订单处理(一天)
     **/
    private static final Long OVER_TIME = 60 * 60 * 24L;

    @Autowired
    private WxPayProperty wechatProperty;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private WxOrderPayService wxOrderPayService;

    /**
     * @Description: 查询处理微信过期的订单
     */
    public void queryWxOverdueOrder() {
        String wxOrderKey = wechatProperty.getWechatOrderKey();
        Long wxOrderExpire = wechatProperty.getWechatOrderExpire();

        // 获取所有订单key
        Set<String> keys = redisUtils.scan(wxOrderKey + "*");
        if (keys.isEmpty()) {
            logger.debug("[WechatOrderQueryTask] -> 暂无订单处理");
            return;
        }
        // 获取所有订单的值
        List<Object> orderList = redisUtils.getValueByKeys(keys);
        for (Object order : orderList) {
            if (order == null) {
                logger.error("[WechatOrderQueryTask] -> 订单信息缓存存在空数据");
                continue;
            }
            WechatOrderCache wxOrder;
            try {
                wxOrder = FastJsonUtils.toBean((String) order, WechatOrderCache.class);
            } catch (Exception e) {
                logger.error("[WechatOrderQueryTask] -> 订单信息转化实体出错，{}", e.getMessage());
                continue;
            }
            String orderNo = wxOrder.getOrderNo();
            String outTradeNo = wxOrder.getOutTradeNo();
            String userId = wxOrder.getUserId();
            String payType = wxOrder.getPayType();
            int isHandle = wxOrder.getIsHandle();
            Long orderTime = wxOrder.getOrderTime();
            Long nowTime = new Date().getTime();

            // 已经处理或者正在处理的订单
            if (isHandle != IS_HANDLE.NO.value()) {
                logger.error("[WechatOrderQueryTask] -> 订单{}已处理", orderNo);
                continue;
            }

            // 未超时的未处理订单
            if (nowTime - orderTime < OVER_TIME) {
                logger.error("[WechatOrderQueryTask] -> 订单{}已经{}s未处理", orderNo, (nowTime - orderTime) / 1000);
                continue;
            }

            // 未处理且超时的订单
            logger.debug("[WechatOrderQueryTask] -> 正在处理订单号为{}的{}类型的订单", orderNo, payType);
            // 设置执行中状态
            wxOrder.setIsHandle(IS_HANDLE.HANDLING.value());
            redisUtils.set(wxOrderKey + outTradeNo, FastJsonUtils.toJSONFeatures(wxOrder), wxOrderExpire);
            try {
                if (PayType.ORDER_PAY.getCode().equals(payType)) {
                    wxOrderPayService.dealUnHandleOrder(outTradeNo, userId, payType);
                } else if (PayType.RECHARGE.getCode().equals(payType)) {
                    rechargeService.dealUnHandleRecharge(outTradeNo, userId, payType);
                } else {
                    logger.error("[WechatOrderQueryTask] -> 订单{}未知类型", orderNo);
                    throw new SuperBootException("lhq-superboot-wechat-0007", orderNo);
                }
                // 执行异常设置为已执行
                wxOrder.setIsHandle(IS_HANDLE.YES.value());
                redisUtils.set(wxOrderKey + outTradeNo, FastJsonUtils.toJSONFeatures(wxOrder), wxOrderExpire);
            } catch (Exception e) {
                logger.error("[WechatOrderQueryTask] -> 订单{}处理异常：{}", orderNo, e.getMessage());
                // 执行异常设置为未执行
                wxOrder.setIsHandle(IS_HANDLE.NO.value());
                redisUtils.set(wxOrderKey + outTradeNo, FastJsonUtils.toJSONFeatures(wxOrder), wxOrderExpire);
            }
        }
    }
}
