package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 支付必要参数
 * @author: lihaoqi
 * @date: 2019年7月3日 下午11:14:54
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatRefundParam {

    /**
     * 商户订单号(必要参数)
     **/
    private String orderNo;

    /**
     * 交易订单号
     **/
    private String outTradeNo;

    /**
     * 商户退款订单号(必要参数)
     **/
    private String refundNo;

    /**
     * 金额（分）(必要参数)
     **/
    private String money;

    /**
     * 退款原因(若订单退款金额≤1元，且属于部分退款，则不会在退款消息中体现退款原因)
     **/
    private String refundDesc;

    /**
     * 通知地址(非必要参数、可在商户平台上配置)
     **/
    private String notifyUrl;

}
