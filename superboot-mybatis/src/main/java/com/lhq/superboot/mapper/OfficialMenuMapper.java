package com.lhq.superboot.mapper;

import com.lhq.superboot.domain.OfficialMenu;
import com.lhq.superboot.domain.OfficialMenuExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface OfficialMenuMapper {
    long countByExample(OfficialMenuExample example);

    int deleteByExample(OfficialMenuExample example);

    int deleteByPrimaryKey(String menuId);

    int insert(OfficialMenu record);

    int insertSelective(OfficialMenu record);

    List<OfficialMenu> selectByExample(OfficialMenuExample example);

    OfficialMenu selectByPrimaryKey(String menuId);

    int updateByExampleSelective(@Param("record") OfficialMenu record, @Param("example") OfficialMenuExample example);

    int updateByExample(@Param("record") OfficialMenu record, @Param("example") OfficialMenuExample example);

    int updateByPrimaryKeySelective(OfficialMenu record);

    int updateByPrimaryKey(OfficialMenu record);
}