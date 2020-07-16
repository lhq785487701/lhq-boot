package com.lhq.superboot.enums;

/**
 * @Description: 阿里获取短信验证码状态
 * @author: lihaoqi
 * @date: 2019年4月29日
 * @version: 1.0.0
 */
public enum AliSMSState {

    WAIT(1, "等待回执"),

    ERROR(2, "发送失败"),

    SUCCESS(3, "发送成功");

    private final String value;
    private final Integer code;

    AliSMSState(int code, String value) {
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
        AliSMSState[] aliSMSStates = values();
        for (AliSMSState aliSMSState : aliSMSStates) {
            if (aliSMSState.code.equals(code)) {
                return aliSMSState.value;
            }
        }
        return null;
    }
}
