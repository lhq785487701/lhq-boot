package com.lhq.superboot.enums;

/**
 * @Description: 微信通知返回值code
 * @author: lihaoqi
 * @date: 2019年7月4日 下午2:23:55
 * @version: v1.0.0
 */
public enum NotifyCode {

    SUCCESS(1000, "请求成功,支付成功"),

    SIGN_FAIL(1001, "签名校验失败"),

    PAY_FAIL(1002, "支付失败"),

    PAY_MONEY_FAIL(1003, "金额校验失败"),

    HANDLED(1004, "该订单号已经处理");

    private final String value;
    private final Integer code;

    NotifyCode(int code, String value) {
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
        NotifyCode[] notifyCode = values();
        for (NotifyCode notifyCodeEnum : notifyCode) {
            if (notifyCodeEnum.code.equals(code)) {
                return notifyCodeEnum.value;
            }
        }
        return null;
    }
}
