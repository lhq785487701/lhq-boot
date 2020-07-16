package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.SMSSendDetail;
import com.lhq.superboot.domain.SMSSendDetailExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SMSSendDetailMapper {
    long countByExample(SMSSendDetailExample example);

    int deleteByExample(SMSSendDetailExample example);

    int deleteByPrimaryKey(Integer smsSendDetailId);

    int insert(SMSSendDetail record);

    int insertSelective(SMSSendDetail record);

    List<SMSSendDetail> selectByExample(SMSSendDetailExample example);

    SMSSendDetail selectByPrimaryKey(Integer smsSendDetailId);

    int updateByExampleSelective(@Param("record") SMSSendDetail record, @Param("example") SMSSendDetailExample example);

    int updateByExample(@Param("record") SMSSendDetail record, @Param("example") SMSSendDetailExample example);

    int updateByPrimaryKeySelective(SMSSendDetail record);

    int updateByPrimaryKey(SMSSendDetail record);
}