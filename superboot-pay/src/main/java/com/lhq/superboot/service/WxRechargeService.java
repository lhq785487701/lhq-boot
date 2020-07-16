package com.lhq.superboot.service;

import com.lhq.superboot.vo.RechargeVo;

/**
 * @Description 微信充值token
 * @author: lihaoqi
 * @date: 2019年7月18日 下午4:03:02
 * @version: v1.0.0
 */
public interface WxRechargeService {

    /**
     * @param rechargeVo 充值参数
     * @return
     * @Description: 微信下单充值
     */
    public String rechargeOrder(RechargeVo rechargeVo);

    /**
     * @return 返回成功xml
     * @Description: 微信支付通知接口
     */
    public String rechargeNotify();

    /**
     * @param userId
     * @param orderNo
     * @param payType
     * @Description: 处理未处理且超时的订单
     */
    public void dealUnHandleRecharge(String orderNo, String userId, String payType) throws Exception;
}
