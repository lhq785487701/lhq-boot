package com.lhq.superboot.enums;

/**
 * @Description: 微信通知返回值code
 * @author: lihaoqi
 * @date: 2019年7月4日 下午2:23:55
 * @version: v1.0.0
 */
public enum OrderCode {

    SUCCESS(1000, "查询订单信息成功，支付成功"),

    PARAM_ERROR(1001, "错误代码见返回的return_msg值"),

    SIGN_ERROR(1002, "校验签名失败"),

    TRADE_FAIL(1003, "支付失败，trade_state不为 SUCCESS"),

    ORDER_NOT_EQUAL(1004, "订单号不匹配");

    private final String value;
    private final Integer code;

    OrderCode(int code, String value) {
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
        OrderCode[] orderCode = values();
        for (OrderCode orderCodeEnum : orderCode) {
            if (orderCodeEnum.code.equals(code)) {
                return orderCodeEnum.value;
            }
        }
        return null;
    }
}