package com.lhq.superboot.vo;

import java.util.Date;

import com.lhq.superboot.domain.SMSSendDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019年8月20日 下午1:49:08
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSSendDetailVo {

    private Integer smsSendDetailId;
    private Integer smsSendLogId;
    private String content;
    private String errCode;
    private String outId;
    private String phone;
    private Date receiveDate;
    private Date sendDate;
    private String sendStatus;
    private String templateCode;

    public static SMSSendDetailVo convert(SMSSendDetail smsDetail) {
        String status = null;
        if (smsDetail.getSendStatus() == 1) {
            status = "等待回执";
        } else if (smsDetail.getSendStatus() == 2) {
            status = "发送失败";
        } else {
            status = "发送成功";
        }

        return new SMSSendDetailVo().toBuilder()
                .smsSendDetailId(smsDetail.getSmsSendDetailId())
                .smsSendLogId(smsDetail.getSmsSendLogId())
                .content(smsDetail.getContent())
                .errCode(smsDetail.getErrCode())
                .outId(smsDetail.getOutId())
                .phone(smsDetail.getPhone())
                .receiveDate(smsDetail.getReceiveDate())
                .sendDate(smsDetail.getSendDate())
                .sendStatus(status)
                .templateCode(smsDetail.getTemplateCode()).build();
    }
}
