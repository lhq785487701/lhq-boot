package com.lhq.superboot.repository;

import java.util.List;

import com.lhq.superboot.domain.Menu;
import com.lhq.superboot.domain.Resource;

/**
 * @Description: 资源(接口权限与菜单权限)数据访问层
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
public interface ResRepository {

    /**
     * @param roleId
     * @return
     * @Description: 资源权限查询
     */
    public List<Resource> selectResByRoleId(String roleId);

    /**
     * @param userId
     * @return
     * @Description: 菜单权限查询
     */
    public List<Menu> selectMenuByUserId(String userId);
}
