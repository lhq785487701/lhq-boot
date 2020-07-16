package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 订单下单返回对象
 * @author: lihaoqi
 * @date: 2019年9月4日 下午11:12:53
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayRtn {

    /**
     * 支付类型
     **/
    private String orderPayType;

    /**
     * 微信预下单返回json字符串
     **/
    private String wxPreOrderStr;
}
