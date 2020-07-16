package com.lhq.superboot.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lhq.superboot.domain.UserNotice;
import com.lhq.superboot.domain.UserNoticeExample;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DELETE;
import com.lhq.superboot.enums.ConstEnumUtils.IS_READ;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.UserNoticeMapper;
import com.lhq.superboot.service.NoticeService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;

/**
 * @Description: 用户通知实现
 * @author: lihaoqi
 * @date: 2019年7月5日 上午11:22:34
 * @version: v1.0.0
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger logger = LoggerFactory.getLogger(NoticeService.class);

    /**
     * 记录用户未读通知数量的Key
     **/
    private static final String NOTICE_KEY = "lhq:superboot:user:notice:";

    @Autowired
    private UserService userService;

    @Autowired
    private UserNoticeMapper noticeMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional
    public void addNotice(String title, String content, String type, Integer priority) {
        logger.debug("[NoticeService] -> 新增一条用户通知");
        String userId = getUserId();

        UserNotice notice = new UserNotice().toBuilder().noticeTitle(title).noticeContent(content).noticeType(type)
                .priority(priority).userId(userId).isRead(IS_READ.NO.value()).isDeleted(IS_DELETE.NO.value()).build();
        noticeMapper.insertSelective(notice);

        // 存储未读数量入redis
        Long userUnreadCount = setUnReadCountToRedis(userId, 1, true);
        logger.debug("[NoticeService] -> 用户{} 存在{}条未读信息", userId, userUnreadCount);
    }

    @Override
    @Transactional
    public void updateNoticeReaded(List<String> noticeIdList) {
        logger.debug("[NoticeService] -> 更新用户通知");
        String userId = getUserId();

        UserNoticeExample noticeExample = new UserNoticeExample();
        noticeExample.createCriteria().andUserIdEqualTo(userId).andIsDeletedEqualTo(IS_DELETE.NO.value());
        UserNotice notice = new UserNotice().toBuilder().isRead(IS_READ.YES.value()).build();
        int size = noticeMapper.updateByExampleSelective(notice, noticeExample);
        logger.debug("[NoticeService] -> 更新数量：{}", size);

        // 存储未读数量入redis
        Long userUnreadCount = setUnReadCountToRedis(userId, size, false);
        logger.debug("[NoticeService] -> 用户{} 存在{}条未读信息", userId, userUnreadCount);
    }

    @Override
    @Transactional
    public void deleteNotice(List<String> noticeIdList) {
        logger.debug("[NoticeService] -> 删除用户通知");
        String userId = getUserId();

        // 获取删除列表中存在的未读信息
        UserNoticeExample countExample = new UserNoticeExample();
        countExample.createCriteria().andIsDeletedEqualTo(IS_DELETE.NO.value()).andIsReadEqualTo(IS_READ.NO.value())
                .andUserIdEqualTo(userId).andNoticeIdIn(noticeIdList);
        int unreadSize = (int) noticeMapper.countByExample(countExample);
        logger.debug("[NoticeService] -> 删除记录中存在未读记录数量：{}", unreadSize);

        // 删除用户通知、并标注已读
        UserNoticeExample noticeExample = new UserNoticeExample();
        noticeExample.createCriteria().andUserIdEqualTo(userId).andIsDeletedEqualTo(IS_DELETE.NO.value());
        UserNotice notice = new UserNotice().toBuilder().isDeleted(IS_DELETE.YES.value()).isRead(IS_READ.YES.value()).build();
        int delSize = noticeMapper.updateByExampleSelective(notice, noticeExample);
        logger.debug("[NoticeService] -> 删除数量：{}", delSize);

        // 存储未读数量入redis
        Long userUnreadCount = setUnReadCountToRedis(userId, unreadSize, false);
        logger.debug("[NoticeService] -> 用户{} 存在{}条未读信息", userId, userUnreadCount);

    }

    @Override
    public Page<UserNotice> queryUserNoticeByPage(String noticeName, boolean isUnread, int pageNum, int pageSize) {
        logger.debug("[NoticeService] -> 分页模糊查询用户通知列表");
        String userId = getUserId();

        PageHelper.startPage(pageNum, pageSize);
        UserNoticeExample noticeExample = new UserNoticeExample();
        UserNoticeExample.Criteria criteria = noticeExample.createCriteria();
        criteria.andUserIdEqualTo(userId).andIsDeletedEqualTo(IS_DELETE.NO.value());
        if (isUnread) {
            criteria.andIsReadEqualTo(IS_READ.NO.value());
        }

        return (Page<UserNotice>) noticeMapper.selectByExample(noticeExample);
    }

    @Override
    public Long queryNoticeUnreadCount() {
        String userId = getUserId();

        // 查询数量
        Long userUnreadCount = setUnReadCountToRedis(userId, 0, true);
        logger.debug("[NoticeService] -> 用户{} 存在{}条未读信息", userId, userUnreadCount);
        return userUnreadCount;
    }

    /**
     * @param userId 用户
     * @param size   通知数量,当size=0时默认返回数量
     * @param isAdd  是否增加，true则增加，false则减少
     * @return 返回数量
     * @Description: 将用户未读通知数量记录，方便查询未读记录
     */
    private Long setUnReadCountToRedis(String userId, int size, boolean isAdd) {
        Object userUnreadCount = redisUtils.get(NOTICE_KEY + userId);
        Long count = 0L;
        // 不存在则回数据库查询并写入redis，存在则原基础上加上修改的数量
        if (userUnreadCount == null) {
            UserNoticeExample noticeExample = new UserNoticeExample();
            noticeExample.createCriteria().andIsDeletedEqualTo(IS_DELETE.NO.value())
                    .andIsReadEqualTo(IS_READ.NO.value()).andUserIdEqualTo(userId);
            count = noticeMapper.countByExample(noticeExample);
            redisUtils.set(NOTICE_KEY + userId, count);
        } else {
            count = (Long) userUnreadCount;
            if (size == 0) {
                return count;
            }
            if (isAdd) {
                redisUtils.incr(NOTICE_KEY + userId, size);
                count += size;
            } else {
                redisUtils.decr(NOTICE_KEY + userId, size);
                count -= size;
            }
        }
        return count;
    }

    /**
     * @return userId
     * @Description: 获取用户id
     */
    private String getUserId() {
        String userId = userService.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        return userId;
    }

}
