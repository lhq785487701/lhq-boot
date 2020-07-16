package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.SysApiLog;
import com.lhq.superboot.domain.SysApiLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysApiLogMapper {
    long countByExample(SysApiLogExample example);

    int deleteByExample(SysApiLogExample example);

    int deleteByPrimaryKey(Integer apiLogId);

    int insert(SysApiLog record);

    int insertSelective(SysApiLog record);

    List<SysApiLog> selectByExample(SysApiLogExample example);

    SysApiLog selectByPrimaryKey(Integer apiLogId);

    int updateByExampleSelective(@Param("record") SysApiLog record, @Param("example") SysApiLogExample example);

    int updateByExample(@Param("record") SysApiLog record, @Param("example") SysApiLogExample example);

    int updateByPrimaryKeySelective(SysApiLog record);

    int updateByPrimaryKey(SysApiLog record);
}