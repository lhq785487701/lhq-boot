package com.lhq.superboot.service;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.domain.WxOrderLog;

/**
 * @Description: 账户积分金额接口
 * @author: lihaoqi
 * @date: 2019年7月8日 上午11:22:20
 * @version: v1.0.0
 */
public interface AccountService {

    /**
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 查询用户金额流水记录
     */
    public Page<PayLog> queryUserPayLog(int pageNum, int pageSize);

    /**
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 查询用户微信支付时下单记录
     */
    public Page<WxOrderLog> queryUserWxOrderLog(int pageNum, int pageSize);

}
