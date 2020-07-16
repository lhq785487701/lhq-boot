package com.lhq.superboot.vo;

import com.lhq.superboot.domain.SMSTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 短信模板
 * @author: lihaoqi
 * @date: 2019年6月11日 下午3:09:57
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSTemplateVo {

    // 短信模板
    private String templateId;
    private String templateName;
    private String templateCode;
    private String templateType;
    private String templateContent;
    private String templateParamName;
    private String templateFixParam;
    private String remark;
    private Integer templateState;

    // 用户模板一些字段
    private Integer isDefault;
    private String userId;

    public static SMSTemplateVo convert(SMSTemplate smsTemplate) {
        return new SMSTemplateVo().toBuilder().templateCode(smsTemplate.getTemplateCode())
                .templateContent(smsTemplate.getTemplateContent()).templateId(smsTemplate.getTemplateId())
                .templateName(smsTemplate.getTemplateName())
                .templateState(smsTemplate.getTemplateState())
                .templateType(smsTemplate.getTemplateType()).templateFixParam(smsTemplate.getTemplateFixParam()).remark(smsTemplate.getRemark()).build();
    }

}
