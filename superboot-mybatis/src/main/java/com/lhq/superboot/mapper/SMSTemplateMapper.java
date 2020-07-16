package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.domain.SMSTemplateExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SMSTemplateMapper {
    long countByExample(SMSTemplateExample example);

    int deleteByExample(SMSTemplateExample example);

    int deleteByPrimaryKey(String templateId);

    int insert(SMSTemplate record);

    int insertSelective(SMSTemplate record);

    List<SMSTemplate> selectByExample(SMSTemplateExample example);

    SMSTemplate selectByPrimaryKey(String templateId);

    int updateByExampleSelective(@Param("record") SMSTemplate record, @Param("example") SMSTemplateExample example);

    int updateByExample(@Param("record") SMSTemplate record, @Param("example") SMSTemplateExample example);

    int updateByPrimaryKeySelective(SMSTemplate record);

    int updateByPrimaryKey(SMSTemplate record);
}