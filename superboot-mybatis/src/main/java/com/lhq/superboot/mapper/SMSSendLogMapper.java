package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.domain.SMSSendLogExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SMSSendLogMapper {
    long countByExample(SMSSendLogExample example);

    int deleteByExample(SMSSendLogExample example);

    int deleteByPrimaryKey(Integer smsSendLogId);

    int insert(SMSSendLog record);

    int insertSelective(SMSSendLog record);

    List<SMSSendLog> selectByExample(SMSSendLogExample example);

    SMSSendLog selectByPrimaryKey(Integer smsSendLogId);

    int updateByExampleSelective(@Param("record") SMSSendLog record, @Param("example") SMSSendLogExample example);

    int updateByExample(@Param("record") SMSSendLog record, @Param("example") SMSSendLogExample example);

    int updateByPrimaryKeySelective(SMSSendLog record);

    int updateByPrimaryKey(SMSSendLog record);
}