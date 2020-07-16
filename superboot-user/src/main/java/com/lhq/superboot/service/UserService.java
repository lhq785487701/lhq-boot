package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Manager;
import com.lhq.superboot.domain.Menu;
import com.lhq.superboot.domain.User;
import com.lhq.superboot.domain.UserConsumer;
import com.lhq.superboot.domain.UserManager;
import com.lhq.superboot.domain.UserRole;
import com.lhq.superboot.qo.UserRegisterQo;
import com.lhq.superboot.vo.PcLoginVo;

/**
 * @Description: 用户接口
 * @author: lihaoqi
 * @date: 2019年5月19日
 * @version: 1.0.0
 */
public interface UserService {

    /**
     * @param userId
     * @return
     * @Description: 通过用户Id查询用户和用户角色
     */
    public UserRole getUserRoleByUserId(String userId);

    /**
     * @param pcLoginVo
     * @return
     * @Description: 通过用户名和渠道为PC/HT获取用户信息
     */
    public UserRole getUserByUsername(PcLoginVo pcLoginVo);


    /**
     * @param userId
     * @return
     * @Description: 获取用户的公钥
     */
    public String getPublicKey(String userId);

    /**
     * @param phone
     * @return
     * @Description: 通过手机获取微信用户
     */
    public UserConsumer getWechatUserByPhone(String phone);

    /**
     * @param phone
     * @return
     * @Description: 通过手机获取pc用户
     */
    public Manager getPcManagerByPhone(String phone);

    /**
     * @param phone
     * @return
     * @Description: 通过手机获取ht用户
     */
    public Manager getHtManagerByPhone(String phone);

    /**
     * @param phone
     * @return
     * @Description: 通过来源与手机号获取用户信息
     */
    public Manager getManagerByPhone(String phone, String channelFlg);

    /**
     * @return String
     * @Description: 获取当前登录用户Id
     */
    public String getCurrentUserId();

    /**
     * @return User
     * @Description: 获取当前登录用户
     */
    public User getCurrentUser();

    /**
     * @return UserManager
     * @Description: 获取当前用户(PC或后台)
     */
    public UserManager getCurrentManager();

    /**
     * @return UserConsumer
     * @Description: 获取当前用户(小程序)
     */
    public UserConsumer getCurrentConsumer();

    /**
     * @param userId
     * @return
     * @Description: 通过用户id获取用户
     */
    public User getUserByUserId(String userId);

    /**
     * @param phone
     * @param wechatId
     * @return
     * @Description: 微信小程序注册用户
     */
    public boolean registerWechatUser(String phone, String wechatId);

    /**
     * @param phone
     * @return
     * @Description: app注册用户
     */
    public boolean registerAppUser(String phone);

    /**
     * @param oldPassword
     * @return
     * @Description: 校验密码是否一致
     */
    public boolean validPwd(String oldPassword);

    /**
     * @param newPassword
     * @return
     * @Description: 更新密码
     */
    public boolean updatePwd(String newPassword);

    /**
     * @return
     * @Description: 查询用户
     */
    public List<Menu> getMenuByUser();

    /**
     * @param phone
     * @param userName
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 分页查询用户信息
     */
    public Page<UserRole> selectUserByPage(String phone, String userName, int pageNum, int pageSize);

    /**
     * @param users
     * @param roleId
     * @return
     * @Description: 用户授权
     */
    public boolean updateUserRole(List<String> users, String roleId);

    /**
     * @param userRegisterQo
     * @return
     * @Description: 注册用户(可pc或ht)
     */
    public String registerUser(UserRegisterQo userRegisterQo);

    /**
     * @param wechatId
     * @return
     * @Description: 通过wechat_id获取用户
     */
    public User getUserByWechatId(String wechatId);

    /**
     * @param userName
     * @param channelFlg
     * @return
     * @Description: 根据渠道验证用户名是否存在
     */
    public boolean checkUsername(String userName, String channelFlg);

    /**
     * @param phone
     * @param channelFlg
     * @return
     * @Description: 根据渠道验证手机号是否存在
     */
    public boolean checkPhone(String phone, String channelFlg);

    /**
     * @param phone
     * @param password
     * @param channelFlg
     * @return
     * @Description: 通过手机修改用户密码
     */
    public boolean updatePwdByPhone(String phone, String password, String channelFlg);

    /**
     * @param phone
     * @param userName
     * @return 回consumerid
     * @Description: 判断小程序中该手机的用户是否已经存在，不存在创建小程序用户
     */
    public String getConsumerIdByPhone(String phone, String userName);

    /**
     * @param phone
     * @return
     * @Description: 更新当前用户的手机
     */
    public boolean updatePhoneByCurrentUser(String phone);

    /**
     * @param managerIdList
     * @return
     * @Description: 通过managerId列表获取usermanager对象
     */
    public List<UserManager> getManagerList(List<String> managerIdList);

    /**
     * @param userList
     * @return
     * @Description: 检查用户是否是管理员身份，如果是管理员身份，无法被修改权限
     */
    public boolean checkUserRole(List<String> userList);

    /**
     * @param userIdList
     * @param isEnabled
     * @Description: 禁用或解禁用户
     */
    public void disableUser(List<String> userIdList, Integer isEnabled);

    /**
     * @param userIdList
     * @Description: 注销用户
     */
    public void deleteUser(List<String> userIdList);

}
