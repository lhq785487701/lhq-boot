package com.lhq.superboot.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lhq.superboot.domain.Manager;
import com.lhq.superboot.domain.UserConsumer;
import com.lhq.superboot.domain.UserManager;
import com.lhq.superboot.domain.UserRole;

/**
 * @Description: 用户数据访问层
 * @author: lihaoqi
 * @date: 2019年5月19日
 * @version: 1.0.0
 */
public interface UserRepository {

    /**
     * @param param
     * @return
     * @Description: 查询用户和权限通过username
     */
    public UserRole selectUserAndRoleByUsername(Map<String, Object> param);

    /**
     * @param phone
     * @return
     * @Description: 通过手机获取微信用户
     */
    public UserConsumer selectWechatUserByPhone(String phone);

    /**
     * @param userId
     * @return
     * @Description: 通过user_id获取用户与权限信息
     */
    public UserRole selectUserAndRoleByUserId(String userId);

    /**
     * @param phone
     * @return
     * @Description: 通过手机获取pc或者ht用户信息
     */
    public Manager selectManagerByPhone(@Param("phone") String phone, @Param("channelFlg") String channelFlg);

    /**
     * @param param
     * @return
     * @Description: 通过条件查询用户列表
     */
    public List<UserRole> selectUserList(Map<String, Object> param);

    /**
     * @param userId
     * @return
     * @Description: 查询当前pc/ht用户
     */
    public UserManager selectCurrentManager(String userId);

    /**
     * @param managerIdList
     * @return
     * @Description: 通过managerid列表查询用户
     */
    public List<UserManager> selectManagerList(List<String> managerIdList);

    /**
     * @param userId
     * @return
     * @Description: 获取当前小程序用户
     */
    public UserConsumer selectCurrentConsumer(String userId);

    /**
     * @param roleResId
     * @return
     * @Description: 通过资源权限Id，获取那些用户与这条权限记录有关，从而删除这部分用户的资源权限缓存,获取用户userId
     */
    public List<String> selectUserByRoleResId(String roleResId);
}
