package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Menu;

/**
 * @Description: 菜单相关接口
 * @author: lihaoqi
 * @date: 2019年5月6日
 * @version: 1.0.0
 */
public interface MenuService {

    /**
     * @param menuName
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 分页获取菜单列表
     */
    public Page<Menu> selectMenuByPage(String menuName, int pageNum, int pageSize);

    /**
     * @param menu
     * @Description: 创建一个菜单
     */
    public void createMenu(Menu menu);

    /**
     * @param menu
     * @Description: 修改菜单信息
     */
    public void updateMenu(Menu menu);

    /**
     * @param menuIdList
     * @param isEnabled
     * @Description: 禁用菜单
     */
    public void disableMenu(List<String> menuIdList, Integer isEnabled);

    /**
     * @param menuIdList
     * @Description: 删除菜单
     */
    public void deleteMenu(List<String> menuIdList);

    /**
     * @param roleId
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 获取对应权限所有的菜单列表
     */
    public Page<Menu> selectRoleMenu(String roleId, int pageNum, int pageSize);

    /**
     * @param roleId
     * @param menuIdList
     * @Description: 新增菜单权限对应关系
     */
    public void createRoleMenu(String roleId, List<String> menuIdList);

    /**
     * @param roleMenuIdList
     * @param isEnabled
     * @Description: 禁用菜单权限对应关系
     */
    public void disableRoleMenu(List<String> roleMenuIdList, Integer isEnabled);

    /**
     * @param roleMenuIdList
     * @Description: 删除菜单权限对应关系
     */
    public void deleteRoleMenu(List<String> roleMenuIdList);
}
