package com.lhq.superboot.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PayLog {
    private String payId;

    private String outTradeNo;

    private String orderNo;

    private Long payPrice;

    private String userId;

    private Date payTime;

    private String payType;

    private String bankType;

    private String remarks;

    private Date createTime;

    private Date modifyTime;

    private Integer isValid;

}