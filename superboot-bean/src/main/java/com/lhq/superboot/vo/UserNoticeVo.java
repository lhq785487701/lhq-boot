package com.lhq.superboot.vo;

import java.util.Date;

import com.lhq.superboot.domain.UserNotice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户通知dto
 * @author: lihaoqi
 * @date: 2019年7月9日 上午1:52:02
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserNoticeVo {

    private String noticeId;
    private String noticeTitle;
    private String noticeContent;
    private String noticeType;
    private Integer priority;
    private Integer isRead;
    private Date readTime;

    public static UserNoticeVo convert(UserNotice userNotice) {
        UserNoticeVo userNoticeDto = new UserNoticeVo().toBuilder().noticeId(userNotice.getNoticeId())
                .noticeTitle(userNotice.getNoticeTitle()).noticeContent(userNotice.getNoticeContent())
                .noticeType(userNotice.getNoticeType()).priority(userNotice.getPriority())
                .isRead(userNotice.getIsRead()).readTime(userNotice.getReadTime()).build();
        return userNoticeDto;
    }

}
