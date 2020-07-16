package com.lhq.superboot.enums;

/**
 * @Description: 预下单日志的状态
 * @author: lihaoqi
 * @date: 2019年7月4日 下午6:50:19
 * @version: v1.0.0
 */
public enum WxOrderState {

    ORDER_SUCCESS("已下单"),

    PAY_SUCCESS("已支付"),

    REFUND_SUCCESS("已退款"),

    ORDER_FAIL("下单失败"),

    PAY_FAIL("支付失败"),

    REFUND_FAIL("退款失败"),

    AUTO_CANCEL("自动取消");

    private String code;

    WxOrderState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
