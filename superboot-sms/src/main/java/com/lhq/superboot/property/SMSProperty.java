package com.lhq.superboot.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Description: 短信参数实体类
 * @author: lihaoqi
 * @date: 2019年4月29日
 * @version: 1.0.0
 */
@Data
@Component
public class SMSProperty {

    @Value("${alibaba.accessKeyId}")
    private String accessKeyId;

    @Value("${alibaba.accessKeySecret}")
    private String accessKeySecret;

    @Value("${alibaba.regionId}")
    private String regionId;

    @Value("${alibaba.sms.domain}")
    private String smsDomain;

    @Value("${alibaba.sms.signName}")
    private String smsSignName;

    @Value("${alibaba.sms.version}")
    private String smsVersion;

    @Value("${alibaba.sms.send.sendSms}")
    private String smsSendCode;

    @Value("${alibaba.sms.send.sendBatchSms}")
    private String smsSendBatchCode;

    @Value("${alibaba.sms.send.paramCode}")
    private String smsParamCode;

    @Value("${alibaba.sms.send.paramContent}")
    private String smsParamContent;

    @Value("${alibaba.sms.send.paramCount}")
    private String smsParamCount;

    @Value("${alibaba.sms.send.paramName}")
    private String smsParamName;

    @Value("${alibaba.sms.send.publicTemplateCode}")
    private String smsPublicTemplateCode;

    @Value("${alibaba.sms.send.giveCardTemplateCode}")
    private String smsGiveCardTemplateCode;

    @Value("${alibaba.sms.send.notifyUserCode}")
    private String notifyUserCode;

    @Value("${alibaba.sms.query.queryCode}")
    private String smsQueryCode;

}
