package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Role;

/**
 * @Description: 权限相关接口
 * @author: lihaoqi
 * @date: 2019年4月23日
 * @version: 1.0.0
 */
public interface RoleService {

    /**
     * @param roleName
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 分页获取权限列表除PC外
     */
    public Page<Role> selectRoleByPage(String roleName, boolean isContainPC, int pageNum, int pageSize);

    /**
     * @param roleName
     * @param roleKey
     * @param roleSort
     * @param dataScope
     * @Description: 创建一个权限
     */
    public void createRole(String roleName, String roleKey, Integer roleSort, Integer dataScope);

    /**
     * @param role
     * @Description: 修改权限内容
     */
    public void updateRole(Role role);

    /**
     * @param roleIdList
     * @param isEnabled
     * @Description: 禁用一个/多个权限
     */
    public void disableRole(List<String> roleIdList, Integer isEnabled);

    /**
     * @param roleIdList
     * @Description: 删除一个/多个权限
     */
    public void deleteRole(List<String> roleIdList);

}
