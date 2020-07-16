package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.SysApiExecute;
import com.lhq.superboot.domain.SysApiExecuteExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysApiExecuteMapper {
    long countByExample(SysApiExecuteExample example);

    int deleteByExample(SysApiExecuteExample example);

    int deleteByPrimaryKey(Integer execId);

    int insert(SysApiExecute record);

    int insertSelective(SysApiExecute record);

    List<SysApiExecute> selectByExample(SysApiExecuteExample example);

    SysApiExecute selectByPrimaryKey(Integer execId);

    int updateByExampleSelective(@Param("record") SysApiExecute record, @Param("example") SysApiExecuteExample example);

    int updateByExample(@Param("record") SysApiExecute record, @Param("example") SysApiExecuteExample example);

    int updateByPrimaryKeySelective(SysApiExecute record);

    int updateByPrimaryKey(SysApiExecute record);
}