package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 支付时的订单缓存实体类
 * @author: lihaoqi
 * @date: 2019年7月10日 下午11:14:54
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatOrderCache {

    private String userId;

    private String orderNo;

    private String outTradeNo;

    private String money;

    private String detail;

    /**
     * 是否处理
     **/
    private Integer isHandle;

    /**
     * 下单时间
     **/
    private Long orderTime;

    /**
     * 支付类型(PayType)
     **/
    private String payType;


}
