package com.lhq.superboot.vo;


import java.util.Date;

import com.lhq.superboot.domain.Company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompanyVo {

    private String companyId;
    private String companyName;
    private String companyCode;
    private String companyLeader;
    private String companyPhone;
    private String companyEmail;
    private String companyAddress;
    private Date createTime;


    public static CompanyVo convert(Company company) {
        CompanyVoBuilder builder = new CompanyVo().toBuilder()
                .companyId(company.getCompanyId())
                .companyName(company.getCompanyName())
                .companyCode(company.getCompanyCode())
                .companyLeader(company.getCompanyLeader())
                .companyPhone(company.getCompanyPhone())
                .companyEmail(company.getCompanyEmail())
                .companyAddress(company.getCompanyAddress())
                .createTime(company.getCreateTime());

        return builder.build();
    }
}
