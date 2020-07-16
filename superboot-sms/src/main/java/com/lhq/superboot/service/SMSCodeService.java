package com.lhq.superboot.service;

import com.lhq.superboot.domain.SMSTemplate;

/**
 * @Description: 短信验证码相关接口
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:28:54
 * @version: v1.0.0
 */
public interface SMSCodeService {

    /**
     * @param phone
     * @Description: 使用默认模板发送验证码
     */
    public void getSMSCode(String phone);

    /**
     * @param phone    发送验证码的手机
     * @param template 发送短信使用的模板
     * @Description: 发送验证码
     */
    public void getSMSCode(String phone, SMSTemplate template);

    /**
     * @param phone
     * @param validCode
     * @return
     * @Description: 将获取到的短信验证码存入redis
     */
    public boolean setCodeInCache(String phone, String validCode);

    /**
     * @param phone
     * @param validCode
     * @return
     * @Description: 验证手机短信验证码
     */
    public boolean validateCode(String phone, String validCode);

    /**
     * @param phone
     * @return
     * @Description: 手机验证码短时间内无法重复发送
     */
    boolean validateCodeRepeatSend(String phone);

}
