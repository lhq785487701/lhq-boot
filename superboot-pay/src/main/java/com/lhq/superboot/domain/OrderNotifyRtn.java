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
public class OrderNotifyRtn {

    /**
     * 充值结果码
     **/
    private Integer notifyCode;

    /**
     * 通知结果，返回给微信
     **/
    private String notifyResult;

    /**
     * 订单No
     **/
    private String orderNo;
}
