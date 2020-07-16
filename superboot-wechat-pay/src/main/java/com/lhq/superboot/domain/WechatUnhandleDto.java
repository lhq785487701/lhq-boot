package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 超时未处理订单处理时微信返回数据实体类
 * @author: lihaoqi
 * @date: 2019年7月12日 上午12:25:32
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatUnhandleDto {

    /**
     * 是否成功支付
     **/
    private boolean isSuccess;

    /**
     * 支付时间
     **/
    private String payTime;

    /**
     * 退款金额
     **/
    private String bankType;

    /**
     * 支付金额
     **/
    private String cashFee;
}
