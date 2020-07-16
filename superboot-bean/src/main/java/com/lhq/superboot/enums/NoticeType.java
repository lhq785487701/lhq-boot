package com.lhq.superboot.enums;

/**
 * @Description: 通知类型
 * @author: lihaoqi
 * @date: 2019年7月5日 下午2:35:35
 * @version: v1.0.0
 */
public enum NoticeType {

    SYSTEM_NOTICE("系统通知"),

    RECHARGE_SUCCESS("充值到账"),

    ORDER_SUCCESS("下单成功"),

    PAY_SUCCESS("支付成功");

    private String code;

    NoticeType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
