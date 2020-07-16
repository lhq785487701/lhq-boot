package com.lhq.superboot.vo;

import java.util.Date;

import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.enums.SMSSendState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 短信日志Vo
 * @author: lihaoqi
 * @date: 2019年8月20日 上午12:52:24
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSSendLogVo {

    private Integer smsSendLogId;
    private String sendPhone;
    private String templateType;
    private String status;
    private String statusDetail;
    private String requestCode;
    private String requestMessage;
    private Date sendTime;
    private String sendCount;
    private String userId;

    public static SMSSendLogVo convert(SMSSendLog smsLog) {
        return new SMSSendLogVo().toBuilder()
                .smsSendLogId(smsLog.getSmsSendLogId())
                .sendPhone(smsLog.getSendPhone())
                .templateType(smsLog.getTemplateType())
                .status(SMSSendState.getDescription(smsLog.getStatus()))
                .statusDetail(smsLog.getStatusDetail())
                .requestCode(smsLog.getRequestCode())
                .requestMessage(smsLog.getRequestMessage())
                .sendTime(smsLog.getSendTime())
                .sendCount(smsLog.getSendCount())
                .userId(smsLog.getUserId()).build();
    }
}
