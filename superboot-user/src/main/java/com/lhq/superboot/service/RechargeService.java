package com.lhq.superboot.service;

import com.lhq.superboot.vo.RechargeVo;

/**
 * @Description: 用户充值接口
 * @author: lihaoqi
 * @date: 2019年7月9日 上午12:58:29
 * @version: v1.0.0
 */
public interface RechargeService {

    /**
     * @param rechargeVo
     * @return 再次前后返回前端调用支付
     * @Description: 下单充值
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
