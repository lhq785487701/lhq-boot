package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.domain.PayLogExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PayLogMapper {
    long countByExample(PayLogExample example);

    int deleteByExample(PayLogExample example);

    int deleteByPrimaryKey(String payId);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogExample example);

    PayLog selectByPrimaryKey(String payId);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);
}