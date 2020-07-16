package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 微信通知返回参数
 * @author: lihaoqi
 * @date: 2019年7月4日 下午2:17:25
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatPayNotifyDto {

    /**
     * 返回微信字段，直接将该字段返回给微信通知微信
     **/
    private String result;

    /**
     * 通知结果(请对应NotifyCode枚举的值)
     **/
    private Integer code;

    /**
     * 结果描述
     **/
    private String desc;

    /**
     * 订单号
     **/
    private String orderNo;

    /**
     * 交易订单号
     **/
    private String outTradeNo;

    /**
     * 支付时间
     **/
    private String payTime;

    /**
     * 支付金额
     **/
    private Long money;

    /**
     * 支付银行
     **/
    private String bankType;
}
