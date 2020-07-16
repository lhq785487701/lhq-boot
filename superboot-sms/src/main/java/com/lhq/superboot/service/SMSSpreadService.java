package com.lhq.superboot.service;

import java.util.List;

/**
 * @Description: 短信推广相关接口
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:28:54
 * @version: v1.0.0
 */
public interface SMSSpreadService {

    /**
     * @param phoneList    发送的手机列表
     * @param templateCode 发送模板
     * @Description: 发送短信推广信息(没有参数)
     */
    public void sendSMSSpread(List<String> phoneList, String templateCode);

}
