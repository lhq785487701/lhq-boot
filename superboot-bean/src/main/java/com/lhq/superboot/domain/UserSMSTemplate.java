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
public class UserSMSTemplate {
    private String userTempId;

    private String userId;

    private String companyId;

    private String templateId;

    private String templateType;

    private Date createTime;

    private Date modifyTime;

    private Integer isEnabled;

    private Integer isDefault;

}