package com.lhq.superboot.wechat;

import com.lhq.superboot.domain.WechatPayParam;
import com.lhq.superboot.domain.WxOrderLog;
import com.lhq.superboot.enums.WxOrderState;

import java.util.List;

/**
 * @Description: 微信订单日志接口
 * @author: lihaoqi
 * @date: 2019年7月4日 下午3:29:17
 */
public interface WechatOrderLog {

    /**
     * @param orderNo
     * @Description: 通过订单号查询所有微信订单支付记录
     */
    public List<WxOrderLog> queryWxOrderLogListByOrderNo(String orderNo);

    /**
     * @param wxOrderState
     * @param wxPayParam
     * @param prepay_id
     * @param sign
     * @param errCode
     * @param errReason
     * @Description: 微信小程序统一下单成功后记录订单信息
     */
    public void insertWxOrderLog(WxOrderState wxOrderState, WechatPayParam wxPayParam, String prepay_id, String sign,
                                 String errCode, String errReason);

    /**
     * @param wxOrderState
     * @param transactionId 微信订单号
     * @param outTradeNo
     * @param payTime
     * @param errCode
     * @param errReason
     * @Description: 更新订单日志
     */
    public void updateWxOrderLog(WxOrderState wxOrderState, String transactionId, String outTradeNo, String payTime, String errCode,
                                 String errReason);


    /**
     * @param outTradeNo
     * @param totalFee
     * @return
     * @Description: 判断通知返回的金额是否与传入一样
     */
    public boolean checkTotalFee(String outTradeNo, String totalFee);

}
