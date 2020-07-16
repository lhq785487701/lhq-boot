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
public class SMSSendLog {
    private Integer smsSendLogId;

    private String sendPhone;

    private String sendApi;

    private String templateType;

    private String templateCode;

    private Integer status;

    private String statusDetail;

    private String requestCode;

    private String requestMessage;

    private Date sendTime;

    private String bizId;

    private String sendCount;

    private String userId;

}