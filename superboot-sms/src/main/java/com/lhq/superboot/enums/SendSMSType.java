package com.lhq.superboot.enums;

/**
 * @Description: 发送短信类型
 * @author: lihaoqi
 * @date: 2019年6月3日
 * @version: 1.0.0
 */
public enum SendSMSType {

    SENDSMS("发送短信"),

    SENDUSERCARDSMS("用户赠送发送短信");

    private String code;

    SendSMSType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
