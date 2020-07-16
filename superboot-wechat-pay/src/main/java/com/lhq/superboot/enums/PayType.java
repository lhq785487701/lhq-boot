package com.lhq.superboot.enums;

/**
 * @Description: 支付的类型
 * 使用： 1.微信下单完成后记录微信下单日志
 * 2.支付成功后记录用户token流水
 * 3.微信订单定时器时，通过该参数判断订单所属模块，从而获取订单信息后调用对应模块的业务
 * @author: lihaoqi
 * @date: 2019年7月4日 下午6:13:53
 * @version: v1.0.0
 */
public enum PayType {

    RECHARGE("用户充值"),

    ORDER_PAY("订单支付");

    private String code;

    PayType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
