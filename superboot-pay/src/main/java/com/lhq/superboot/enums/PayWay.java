package com.lhq.superboot.enums;

/**
 * @Description 支付方式
 * @author: lihaoqi
 * @date: 2019年7月18日 下午1:52:05
 * @version: v1.0.0
 */
public enum PayWay {

    WXCHAT("微信支付"),

    TOKEN("积分支付"),

    ALIPAY("支付宝支付");

    private String code;

    PayWay(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
