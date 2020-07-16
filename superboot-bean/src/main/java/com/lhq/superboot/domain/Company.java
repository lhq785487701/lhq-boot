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
public class Company {
    private String companyId;

    private String companyName;

    private String companyCode;

    private String companyLeader;

    private String companyPhone;

    private String companyEmail;

    private String companyAddress;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

    private Integer isEnabled;

    private Integer isDeleted;

}