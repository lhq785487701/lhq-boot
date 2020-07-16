package com.lhq.superboot.mapper;

import java.util.List;

import com.lhq.superboot.domain.WxOrderLog;
import com.lhq.superboot.domain.WxOrderLogExample;
import org.apache.ibatis.annotations.Param;

public interface WxOrderLogMapper {
    long countByExample(WxOrderLogExample example);

    int deleteByExample(WxOrderLogExample example);

    int deleteByPrimaryKey(String orderId);

    int insert(WxOrderLog record);

    int insertSelective(WxOrderLog record);

    List<WxOrderLog> selectByExample(WxOrderLogExample example);

    WxOrderLog selectByPrimaryKey(String orderId);

    int updateByExampleSelective(@Param("record") WxOrderLog record, @Param("example") WxOrderLogExample example);

    int updateByExample(@Param("record") WxOrderLog record, @Param("example") WxOrderLogExample example);

    int updateByPrimaryKeySelective(WxOrderLog record);

    int updateByPrimaryKey(WxOrderLog record);
}