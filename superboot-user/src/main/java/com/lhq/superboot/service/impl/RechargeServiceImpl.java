package com.lhq.superboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.User;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.RechargeService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.service.WxRechargeService;
import com.lhq.superboot.vo.RechargeVo;

/**
 * @Description: 用户充值实现
 * @author: lihaoqi
 * @date: 2019年7月9日 上午1:20:48
 * @version: v1.0.0
 */
@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private UserService userService;

    @Autowired
    private WxRechargeService wxRechargeService;

    @Override
    public String rechargeOrder(RechargeVo rechargeVo) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        rechargeVo.setUserId(user.getUserId());
        rechargeVo.setWechatId(user.getWechatId());

        return wxRechargeService.rechargeOrder(rechargeVo);
    }

    @Override
    public String rechargeNotify() {
        return wxRechargeService.rechargeNotify();
    }

    @Override
    public void dealUnHandleRecharge(String orderNo, String userId, String payType) throws Exception {
        wxRechargeService.dealUnHandleRecharge(orderNo, userId, payType);
    }

}
