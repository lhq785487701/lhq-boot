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
public class WechatPayParam {

    /**
     * 用户id(必要参数)
     **/
    private String userId;

    /**
     * 用户openid(必要参数)
     **/
    private String openid;

    /**
     * 商户订单号(必要参数)
     **/
    private String orderNo;

    /**
     * 商户下单时交易订单号(唯一必须，在pay生成)
     **/
    private String outTradeNo;

    /**
     * 金额（分）(必要参数)
     **/
    private String money;

    /**
     * 商品描述(必要参数)
     **/
    private String body;

    /**
     * 商品详情
     **/
    private String detail;

    /**
     * 商品ID
     **/
    private String productId;

    /**
     * 通知地址(必要参数)
     **/
    private String notifyUrl;

    /**
     * 下单类型(PayType枚举) (必要参数)
     **/
    private String payType;

}
