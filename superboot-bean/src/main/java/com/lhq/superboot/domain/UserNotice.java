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
public class UserNotice {
    private String noticeId;

    private String userId;

    private String noticeTitle;

    private String noticeContent;

    private String noticeType;

    private Integer priority;

    private Integer isRead;

    private Date readTime;

    private Date createTime;

    private Integer isDeleted;

}