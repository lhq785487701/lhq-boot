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
public class SysApiLog {
    private Integer apiLogId;

    private String apikey;

    private String params;

    private String result;

    private String message;

    private String execCode;

    private String method;

    private Integer isasyn;

    private String remark;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

}