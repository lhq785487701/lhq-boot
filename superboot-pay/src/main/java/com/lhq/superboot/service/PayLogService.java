package com.lhq.superboot.service;

/**
 * @Description 金额支付日志实现
 * @author: lihaoqi
 * @date: 2019年7月18日 上午11:31:53
 * @version: v1.0.0
 */
public interface PayLogService {

    /**
     * @param userId
     * @param orderNo
     * @param outTradeNo
     * @param payType
     * @param bankType
     * @param price
     * @param timeEnd
     * @Description: 支付成功记录支付流水
     */
    public void insertPayLog(String userId, String orderNo, String outTradeNo, String payType, String bankType, Long price, String timeEnd);

    /**
     * @param outTradeNo
     * @Description: 退款记录
     */
    public void updateRefundPayLog(String outTradeNo);
}
