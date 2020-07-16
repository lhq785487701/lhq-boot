package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lihaoqi
 * @date: 2019年4月30日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSSendDetailDTO {

    private String sendDate;

    private int sendStatus;

    private String receiveDate;

    private String outId;

    private String errCode;

    private String templateCode;

    private String content;

    private String phoneNum;
}
