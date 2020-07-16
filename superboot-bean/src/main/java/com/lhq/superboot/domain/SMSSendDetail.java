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
public class SMSSendDetail {

    private Integer smsSendDetailId;

    private Integer smsSendLogId;

    private String content;

    private String errCode;

    private String outId;

    private String phone;

    private Date receiveDate;

    private Date sendDate;

    private Integer sendStatus;

    private String templateCode;

}