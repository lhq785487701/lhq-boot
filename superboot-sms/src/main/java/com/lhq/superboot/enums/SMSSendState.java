package com.lhq.superboot.enums;

/**
 * @Description: 发送短信时，发送的状态
 * @author: lihaoqi
 * @date: 2019年8月19日 下午11:11:19
 * @version: v1.0.0
 */
public enum SMSSendState {

    WAIT(0, "正在发送"),

    ERROR(-1, "发送失败"),

    SUCCESS(1, "发送成功");

    private final String value;
    private final Integer code;

    SMSSendState(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getDescription(Integer value) {
        SMSSendState[] enums = values();
        for (SMSSendState entity : enums) {
            if (entity.getCode() == value) {
                return entity.getValue();
            }
        }
        return null;
    }

}
