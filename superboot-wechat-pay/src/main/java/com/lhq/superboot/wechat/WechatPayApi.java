package com.lhq.superboot.wechat;

import com.lhq.superboot.domain.WechatPayNotifyDto;
import com.lhq.superboot.domain.WechatPayParam;
import com.lhq.superboot.domain.WechatQueryOrderDto;
import com.lhq.superboot.domain.WechatRefundDto;
import com.lhq.superboot.domain.WechatRefundParam;
import com.lhq.superboot.domain.WechatUnhandleDto;

import java.util.List;

/**
 * @Description: wechat支付接口
 * @author: lihaoqi
 * @date: 2019年6月27日 下午2:20:43
 * @version: v1.0.0
 */
public interface WechatPayApi {

    /**
     * @return
     * @Description: 微信支付服务后台生成预支付交易单，返回正确的预支付交易后调起支付。
     */
    public String unifiedOrder(WechatPayParam wxPayParam);

    /**
     * @Description: 具体业务根据通知地址具体实现，该方法完成支付后基础校验记录功能。
     * 微信支付通知(微信主动调用) 特别提醒： 1、商户系统对于支付结果通知的内容一定要做签名验证,并校验返回的订单金额是否与商户侧的订单金额一致，
     * 防止数据泄漏导致出现“假通知”，造成资金损失。 2、当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，
     * 如果没有处理过再进行处理，如果处理过直接返回结果成功。 在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 注：目前暂定只有返回微信OK的时候，微信才不会再次发送信息
     */
    public WechatPayNotifyDto wechatPayNotify() throws Exception;

    /**
     * @param wxRefundParam
     * @return
     * @Description: 申请退款(需要证书)
     */
    public WechatRefundDto wechatRefund(WechatRefundParam wxRefundParam);

    /**
     * @param outTradeNo
     * @return
     * @Description: 从微信接口 查询订单信息
     */
    public WechatQueryOrderDto queryOrder(String outTradeNo) throws Exception;

    /**
     * @param outTradeNo
     * @param userId
     * @return WechatUnhandleDto
     * @throws Exception 抛出异常查询失败
     * @Description: 处理未处理且超时的订单
     */
    public WechatUnhandleDto dealUnHandleOrder(String outTradeNo, String userId) throws Exception;

    /**
     * @param orderNo
     * @return
     * @Description: 通过订单号获取所有支付微信支付信息
     */
    public List<WechatQueryOrderDto> queryOrderListByOrderNo(String orderNo);

    /**
     * @param outTradeNo
     * @param orderNo
     * @Description: 设置订单为已处理
     */
    public void setOrderHandled(String outTradeNo, String orderNo);
}
