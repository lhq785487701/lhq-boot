package com.lhq.superboot.service;

import com.lhq.superboot.domain.OrderNotifyRtn;
import com.lhq.superboot.domain.OrderPayRtn;
import com.lhq.superboot.domain.PayParam;

/**
 * @Description: 微信下单支付接口
 * @author: lihaoqi
 * @date: 2019年9月4日 下午5:42:06
 * @version: v1.0.0
 */
public interface WxOrderPayService {

    /**
     * @param payParam
     * @return
     * @Description: 订单下单
     */
    public OrderPayRtn order(PayParam payParam);

    /**
     * @return
     * @Description: 下单微信通知
     */
    public OrderNotifyRtn orderNotify();

    /**
     * @param orderNo
     * @param userId
     * @param payType
     * @throws Exception
     * @Description: 定时器处理还没有处理的微信订单
     */
    public void dealUnHandleOrder(String orderNo, String userId, String payType) throws Exception;
}
