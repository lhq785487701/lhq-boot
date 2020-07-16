package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.UserSMSTemplate;
import com.lhq.superboot.domain.UserSMSTemplateExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserSMSTemplateMapper {
    long countByExample(UserSMSTemplateExample example);

    int deleteByExample(UserSMSTemplateExample example);

    int deleteByPrimaryKey(String userTempId);

    int insert(UserSMSTemplate record);

    int insertSelective(UserSMSTemplate record);

    List<UserSMSTemplate> selectByExample(UserSMSTemplateExample example);

    UserSMSTemplate selectByPrimaryKey(String userTempId);

    int updateByExampleSelective(@Param("record") UserSMSTemplate record, @Param("example") UserSMSTemplateExample example);

    int updateByExample(@Param("record") UserSMSTemplate record, @Param("example") UserSMSTemplateExample example);

    int updateByPrimaryKeySelective(UserSMSTemplate record);

    int updateByPrimaryKey(UserSMSTemplate record);
}