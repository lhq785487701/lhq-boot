package com.lhq.superboot.enums;

/**
 * @Description: 短信模板类型
 * @author: lihaoqi
 * @date: 2019年7月29日 下午12:33:47
 * @version: v1.0.0
 */
public enum SMSTempType {

    SMS_NOTICE("短信通知"),

    SMS_CODE("验证码"),

    SMS_SPREAD("推广短信");

    private String code;

    SMSTempType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
