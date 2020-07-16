package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.UserNotice;

/**
 * @Description: 用户通知
 * @author: lihaoqi
 * @date: 2019年7月5日 上午11:22:20
 * @version: v1.0.0
 */
public interface NoticeService {

    /**
     * @param title
     * @param content
     * @param type     NoticeType中取code值
     * @param priority Noticepriority中取code值
     * @Description: 增加一条通知
     */
    public void addNotice(String title, String content, String type, Integer priority);

    /**
     * @param noticeIdList
     * @Description: 批量更新通知已读
     */
    public void updateNoticeReaded(List<String> noticeIdList);


    /**
     * @param noticeIdList
     * @Description: 批量删除通知
     */
    public void deleteNotice(List<String> noticeIdList);

    /**
     * @param noticeName
     * @param isUnread   是否未读信息
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 分页查询用户通知
     */
    public Page<UserNotice> queryUserNoticeByPage(String noticeName, boolean isUnread, int pageNum, int pageSize);

    /**
     * @return
     * @Description: 查询用户未读记录（先找redis。未找到进入数据库找，存入redis）
     */
    public Long queryNoticeUnreadCount();
}
