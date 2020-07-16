package com.lhq.superboot.enums;

/**
 * @Description: pc端登录方式
 * @author: lihaoqi
 * @date: 2019年11月13日 上午11:07:21
 * @version: v1.0.0
 */
public enum LoginMode {

    // 账号密码登录
    ACCOUNT_PWD("account_pwd"),

    // 手机验证码登录
    PHONE_CODE("phone_code"),

    // 微信扫一扫登录
    WECHAT_SCAN("wechat_scan");

    private String code;

    private LoginMode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
