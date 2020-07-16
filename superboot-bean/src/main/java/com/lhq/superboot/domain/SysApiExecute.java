package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysApiExecute {
    private Integer execId;

    private String execName;

    private String execCode;

    private String execClass;

    private String execMethod;

    private String execParam;

    private String remark;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

    private Integer isEnabled;

    private Integer isDeleted;
}