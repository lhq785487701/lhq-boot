package com.lhq.superboot.service;

import com.lhq.superboot.domain.menu.MenuButton;

/**
 * @Description: 公众号菜单
 * @author: lihaoqi
 * @date: 2019年9月9日 上午11:16:01
 * @version: v1.0.0
 */
public interface OfficialMenuService {

    /**
     * @return
     * @Description: 数据库中初始化小城市公众号的菜单
     */
    public MenuButton initMenu();

    /**
     * @param menu
     * @return
     * @Description: 创建菜单
     */
    public boolean creatMenu(MenuButton menu);

    /**
     * @return 返回json
     * @Description: 获取公众号菜单
     */
    public String getMenu();

    /**
     * @return
     * @Description: 删除菜单
     */
    public boolean deleteMenu();
}
