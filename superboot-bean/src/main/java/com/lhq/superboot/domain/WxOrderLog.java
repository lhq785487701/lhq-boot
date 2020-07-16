package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WxOrderLog {
    private String orderId;

    private String outTradeNo;

    private String orderNo;

    private String userId;

    private String openId;

    private Long totalFee;

    private String sign;

    private String prepayId;

    private String transactionId;

    private String orderState;

    private Date orderTime;

    private Date payTime;

    private String errCode;

    private String errReason;

    private Date createTime;

    private Date modifyTime;

}