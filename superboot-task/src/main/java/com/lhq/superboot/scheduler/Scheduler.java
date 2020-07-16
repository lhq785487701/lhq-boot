package com.lhq.superboot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lhq.superboot.enums.EnvProfile;
import com.lhq.superboot.scheduler.auth.AuthWhiteUrlTask;
import com.lhq.superboot.scheduler.sms.SMSSendLogQueryTask;
import com.lhq.superboot.scheduler.wechat.WechatOrderQueryTask;
import com.lhq.superboot.util.SpringContextUtil;

/**
 * @Description 定时器总控
 * @author: lihaoqi
 * @date: 2019年7月10日 下午6:15:02
 * @version: v1.0.0
 */
@Component
@Configuration
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private WechatOrderQueryTask wxOrderTask;

    @Autowired
    private SMSSendLogQueryTask smsLogTask;

    @Autowired
    private AuthWhiteUrlTask whiteTask;

    /**
     * @Description: 检查微信订单是否已经通知，未通知调用微信查询订单接口
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void WxOrderTasks() {
        logger.info("[Scheduler] -> 检查微信订单定时器");
        wxOrderTask.queryWxOverdueOrder();
    }

    /**
     * @Description: 检查短信发送的结果并记录
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void smsSendTasks() {
        logger.info("[Scheduler] -> 检查发送短信定时器");
        smsLogTask.querySMSSendLogDetail();
    }

    /**
     * @Description: 检查是否权限地址白名单有变动，存在则更新权限(该定时器仅仅当非正式环境使用，正式环境减少压力不执行)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void authWhiteUrl() {
        if (SpringContextUtil.getActiveProfile() != EnvProfile.PROFILE_PROD.getCode()) {
            logger.info("[Scheduler] -> 检查白名单定时器");
            whiteTask.checkWhiteListUrlIsChange();
        }
    }

}
