package com.lhq.superboot.service.impl;

import com.lhq.superboot.domain.WxOrderLog;
import com.lhq.superboot.domain.WxOrderLogExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.domain.PayLogExample;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.PayLogMapper;
import com.lhq.superboot.mapper.WxOrderLogMapper;
import com.lhq.superboot.service.AccountService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.StringUtils;

/**
 * @Description: 账户接口实现
 * @author: lihaoqi
 * @date: 2019年7月10日 上午11:55:53
 * @version: v1.0.0
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private WxOrderLogMapper wxOrderLogMapper;


    @Override
    public Page<PayLog> queryUserPayLog(int pageNum, int pageSize) {
        logger.debug("[AccountService] -> 查询用户支付金额流水");
        PayLogExample payLogExample = new PayLogExample();
        payLogExample.createCriteria().andUserIdEqualTo(getUserId());

        return (Page<PayLog>) payLogMapper.selectByExample(payLogExample);
    }

    @Override
    public Page<WxOrderLog> queryUserWxOrderLog(int pageNum, int pageSize) {
        logger.debug("[AccountService] -> 查询用户微信下单流水");
        WxOrderLogExample wxOrdrExample = new WxOrderLogExample();
        wxOrdrExample.createCriteria().andUserIdEqualTo(getUserId());

        return (Page<WxOrderLog>) wxOrderLogMapper.selectByExample(wxOrdrExample);
    }

    /**
     * @return userId
     * @Description: 获取用户id
     */
    private String getUserId() {
        String userId = userService.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        return userId;
    }

}
