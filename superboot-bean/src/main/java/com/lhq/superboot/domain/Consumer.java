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
public class Consumer {
    private String consumerId;

    private String userId;

    private String consumerPid;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

}