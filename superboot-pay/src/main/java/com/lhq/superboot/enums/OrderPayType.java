package com.lhq.superboot.enums;

/**
 * @Description: 下单支付类型
 * @author: lihaoqi
 * @date: 2019年9月5日 上午11:05:15
 * @version: v1.0.0
 */
public enum OrderPayType {

    //全积分支付
    ALL_TOKEN("ALL_TOKEN"),

    //全金额支付
    ALL_MONEY("ALL_MONEY"),

    //半积分半金额支付
    HALF_TOKEN_MONEY("HALF_TOKEN_MONEY");

    private String code;

    OrderPayType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
