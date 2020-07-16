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
public class SMSTemplate {
    private String templateId;

    private String templateName;

    private String templateCode;

    private String templateType;

    private Integer templateState;

    private String templateContent;

    private String templateParamName;

    private String templateFixParam;

    private String remark;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

    private Integer isEnabled;

    private Integer isDeleted;

}