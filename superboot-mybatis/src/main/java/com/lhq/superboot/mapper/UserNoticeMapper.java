package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.UserNotice;
import com.lhq.superboot.domain.UserNoticeExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserNoticeMapper {
    long countByExample(UserNoticeExample example);

    int deleteByExample(UserNoticeExample example);

    int deleteByPrimaryKey(String noticeId);

    int insert(UserNotice record);

    int insertSelective(UserNotice record);

    List<UserNotice> selectByExample(UserNoticeExample example);

    UserNotice selectByPrimaryKey(String noticeId);

    int updateByExampleSelective(@Param("record") UserNotice record, @Param("example") UserNoticeExample example);

    int updateByExample(@Param("record") UserNotice record, @Param("example") UserNoticeExample example);

    int updateByPrimaryKeySelective(UserNotice record);

    int updateByPrimaryKey(UserNotice record);
}