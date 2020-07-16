package com.lhq.superboot.enums;

/**
 * @Description: 小程序errcode 的合法值
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
public enum WechatErrCode {

    TIMEOUT(-1, "系统繁忙，此时请开发者稍候再试"),

    SUCCESS(0, "请求成功"),

    CODEINVALID(40029, "code无效"),

    FREQUENT(40051, "频率限制，每个用户每分钟100次");

    private final String value;
    private final Integer code;

    WechatErrCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(Integer code) {
        WechatErrCode[] wechatErrCodeEnums = values();
        for (WechatErrCode wechatErrCodeEnum : wechatErrCodeEnums) {
            if (wechatErrCodeEnum.code.equals(code)) {
                return wechatErrCodeEnum.value;
            }
        }
        return null;
    }
}
