package com.lhq.superboot.service.impl;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.RsaUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lhq.superboot.domain.Channel;
import com.lhq.superboot.domain.ChannelExample;
import com.lhq.superboot.domain.Company;
import com.lhq.superboot.domain.CompanyExample;
import com.lhq.superboot.domain.Consumer;
import com.lhq.superboot.domain.Manager;
import com.lhq.superboot.domain.ManagerExample;
import com.lhq.superboot.domain.Menu;
import com.lhq.superboot.domain.User;
import com.lhq.superboot.domain.UserConsumer;
import com.lhq.superboot.domain.UserExample;
import com.lhq.superboot.domain.UserInfo;
import com.lhq.superboot.domain.UserManager;
import com.lhq.superboot.domain.UserRole;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DELETE;
import com.lhq.superboot.enums.ConstEnumUtils.IS_ENABLED;
import com.lhq.superboot.enums.LoginSource;
import com.lhq.superboot.enums.UserMsg;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.ChannelMapper;
import com.lhq.superboot.mapper.CompanyMapper;
import com.lhq.superboot.mapper.ConsumerMapper;
import com.lhq.superboot.mapper.ManagerMapper;
import com.lhq.superboot.mapper.UserInfoMapper;
import com.lhq.superboot.mapper.UserMapper;
import com.lhq.superboot.qo.UserRegisterQo;
import com.lhq.superboot.repository.ResRepository;
import com.lhq.superboot.repository.UserRepository;
import com.lhq.superboot.service.UserApiService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.shiro.LocalRealm;
import com.lhq.superboot.shiro.utils.ShiroMd5Util;
import com.lhq.superboot.util.CheckUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.PcLoginVo;

/**
 * @Description: 用户实现类
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * pc默认权限id
     **/
    private static final String DEFAULT_PC_ROLE_ID = "1";
    /**
     * ht默认权限id
     **/
    private static final String DEFAULT_HT_ROLE_ID = "2";
    /**
     * 后台管理员权限id
     **/
    private static final String DEFAULT_HT_MANAGER_ROLE_ID = "3";
    /**
     * 创建用户初始化钱包数量
     **/
    @SuppressWarnings("unused")
    private static final Long INIT_TOKEN = 0L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ResRepository resRepository;

    @Autowired
    private LocalRealm localRealm;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserApiService userApiService;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${rsa.namespace}")
    private String rsaNamespace;

    @Value("${rsa.time}")
    private long rsaPriExpireTime;

    @Override
    public UserRole getUserRoleByUserId(String userId) {
        return userRepository.selectUserAndRoleByUserId(userId);
    }

    @Override
    public UserConsumer getWechatUserByPhone(String phone) {
        return userRepository.selectWechatUserByPhone(phone);
    }

    @Override
    public Manager getPcManagerByPhone(String phone) {
        return userRepository.selectManagerByPhone(phone, "PC");
    }

    @Override
    public Manager getHtManagerByPhone(String phone) {
        return userRepository.selectManagerByPhone(phone, "HT");
    }

    @Override
    public Manager getManagerByPhone(String phone, String channelFlg) {
        return userRepository.selectManagerByPhone(phone, channelFlg.toUpperCase());
    }

    @Override
    public UserRole getUserByUsername(PcLoginVo pcLoginVo) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("userName", pcLoginVo.getUserName());
        param.put("channelFlg", pcLoginVo.getChannelFlg());
        return userRepository.selectUserAndRoleByUsername(param);
    }

    @Override
    public String getCurrentUserId() {
        try {
            String userId = (String) SecurityUtils.getSubject().getPrincipal();
            if (StringUtils.isEmpty(userId)) {
                return null;
            }
            return userId;
        } catch (Exception e) {
            logger.error("获取用户id失败，原因：{}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getPublicKey(String userId) {

        KeyPair keyPair = RsaUtils.getRSAKeyPair();
        if (keyPair == null) {
            logger.debug("rsa加密报错");
            throw new SuperBootException("rsa加密错误 keypair为空");
        }
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String pri = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        redisUtils.set(rsaNamespace + userId, pri, rsaPriExpireTime);
        return pub;
    }

    @Override
    public User getCurrentUser() {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public UserManager getCurrentManager() {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return userRepository.selectCurrentManager(userId);
    }

    @Override
    public UserConsumer getCurrentConsumer() {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return userRepository.selectCurrentConsumer(userId);
    }

    @Override
    public User getUserByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    @Transactional
    public boolean registerWechatUser(String phone, String wechatId) {
        // 通过手机号与渠道为xcx获取用户
        UserConsumer user = getWechatUserByPhone(phone);

        // 用户不为空且wechatid与获取的wechatid相同，直接登录并返回token即可
        if (user != null && wechatId.equals(user.getUser().getWechatId())) {
            logger.debug("手机号为：{} 的小程序用户已经存在,wechatId一致", phone);
            return true;
        }

        // 用户不为空且wechatid与获取的wechatid不相同，修改最新wechatid，并返回token即可
        if (user != null && !wechatId.equals(user.getUser().getWechatId())) {
            logger.debug("手机号为：{} 的小程序用户已经存在,wechatId不同{},{}", phone, wechatId, user.getUser().getWechatId());
            UserExample userCondition = new UserExample();
            userCondition.createCriteria().andUserIdEqualTo(user.getUser().getUserId());
            userMapper.updateByExampleSelective(new User().toBuilder().wechatId(wechatId).build(), userCondition);
            return true;
        }

        try {
            // 获取渠道信息
            Channel channel = getChannelMsg(LoginSource.XCX.name());

            // 插入用户信息表
            UserInfo userInfo = new UserInfo();
            userInfoMapper.insertSelective(userInfo);
            String userInfoId = userInfo.getUserInfoId();


            // 插入用户表
            User newUser = new User().toBuilder().phone(phone).wechatId(wechatId).channelId(channel.getChannelId())
                    .userInfoId(userInfoId).build();
            userMapper.insertSelective(newUser);

            // 插入一条顾客信息进顾客表
            Consumer consumer = new Consumer().toBuilder().userId(newUser.getUserId()).build();
            consumerMapper.insertSelective(consumer);
            return true;
        } catch (SuperBootException e) {
            logger.error("获取小程序渠道信息异常", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("执行语句异常", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean registerAppUser(String phone) {
        // 通过手机号与渠道为xcx获取用户
        UserConsumer user = getWechatUserByPhone(phone);
        // 用户不为空
        if (user != null) {
            logger.debug("手机号为：{} 的app用户已经存在,wechatId一致", phone);
            return true;
        }

        try {
            // 获取渠道信息
            Channel channel = getChannelMsg(LoginSource.XCX.name());

            // 插入用户信息表
            UserInfo userInfo = new UserInfo();
            userInfoMapper.insertSelective(userInfo);
            String userInfoId = userInfo.getUserInfoId();

            // 插入用户表
            User newUser = new User().toBuilder().phone(phone).channelId(channel.getChannelId()).userInfoId(userInfoId).build();
            userMapper.insertSelective(newUser);

            // 插入一条顾客信息进顾客表
            Consumer consumer = new Consumer().toBuilder().userId(newUser.getUserId()).build();
            consumerMapper.insertSelective(consumer);
            return true;
        } catch (SuperBootException e) {
            logger.error("获取小程序渠道信息异常", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("执行语句异常", e);
            return false;
        }
    }

    @Override
    public boolean validPwd(String oldPassword) {
        User user = getCurrentUser();
        return ShiroMd5Util.PwdMd5(oldPassword).equals(user.getPassword());
    }

    @Override
    @Transactional
    public boolean updatePwd(String newPassword) {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }

        User user = new User().toBuilder().password(ShiroMd5Util.PwdMd5(newPassword)).build();
        UserExample userCondition = new UserExample();
        userCondition.createCriteria().andUserIdEqualTo(userId);
        userMapper.updateByExampleSelective(user, userCondition);

        // 清除缓存数据
        Cache<Object, AuthenticationInfo> authenticationCache = localRealm.getAuthenticationCache();
        if (authenticationCache != null) {
            authenticationCache.remove(userId);
        }
        return true;
    }

    @Override
    public List<Menu> getMenuByUser() {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return null;
        }

        return resRepository.selectMenuByUserId(userId);
    }

    @Override
    public Page<UserRole> selectUserByPage(String phone, String userName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        Map<String, Object> param = new HashMap<>();
        param.put("userName", userName);
        param.put("phone", phone);
        param.put("channel", Arrays.asList("HT"));
        return (Page<UserRole>) userRepository.selectUserList(param);
    }

    @Override
    @Transactional
    public boolean updateUserRole(List<String> users, String roleId) {
        Manager manager = new Manager().toBuilder().roleId(roleId).build();
        ManagerExample managerExample = new ManagerExample();
        managerExample.createCriteria().andUserIdIn(users);
        managerMapper.updateByExampleSelective(manager, managerExample);
        // 清除修改用户的权限缓存数据
        Cache<Object, AuthorizationInfo> authorizationCache = localRealm.getAuthorizationCache();
        if (authorizationCache != null) {
            for (String id : users) {
                authorizationCache.remove(id);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public String registerUser(UserRegisterQo userRegisterQo) {

        String companyId = "";
        if (StringUtils.isNotEmpty(userRegisterQo.getCompanyCode())) {
            CompanyExample companyExample = new CompanyExample();
            companyExample.createCriteria().andCompanyCodeEqualTo(userRegisterQo.getCompanyCode());
            List<Company> company = companyMapper.selectByExample(companyExample);
            if (company.size() == 1) {
                companyId = company.get(0).getCompanyId();
            } else {
                logger.error("不存在该公司:{}", userRegisterQo.getCompanyCode());
            }
        }

        try {
            // 获取渠道信息
            Channel channel = getChannelMsg(userRegisterQo.getChannelFlg());

            String phone = userRegisterQo.getPhone();
            String userName = userRegisterQo.getUserName();
            // 插入一条数据进入manager表，默认给与权限
            String roleId = "";
            if (channel.getChannelFlg().equals(LoginSource.PC.name())) {
                roleId = DEFAULT_PC_ROLE_ID;
            } else if (channel.getChannelFlg().equals(LoginSource.HT.name())) {
                roleId = DEFAULT_HT_ROLE_ID;
            } else {
                throw new SuperBootException("lhq-superboot-user-0012", channel.getChannelFlg());
            }

            // 插入用户信息表
            UserInfo userInfo = new UserInfo();
            userInfoMapper.insertSelective(userInfo);
            String userInfoId = userInfo.getUserInfoId();

            // 插入一条数据进入用户表
            User user = new User().toBuilder().userName(userName).phone(phone)
                    .password(ShiroMd5Util.PwdMd5(userRegisterQo.getPassword())).channelId(channel.getChannelId())
                    .userInfoId(userInfoId).build();
            userMapper.insertSelective(user);

            Manager manager = new Manager().toBuilder().userId(user.getUserId()).managerType(0).companyId(companyId)
                    .roleId(roleId).build();
            managerMapper.insertSelective(manager);

            if (channel.getChannelFlg().equals(LoginSource.HT.name())) {
                // ht用户创建时短信通知用户
                // MsgApi.userCreateSMSNotice(phone, userName, AESUtils.decrypt(userRegisterQo.getOriginalPwd()));
            } else if (channel.getChannelFlg().equals(LoginSource.PC.name())) {
                // pc用户创建用户时生成apiKey
                String apiKey = userApiService.getApiKey(user.getUserId());
                logger.debug("注册pc用户生成用户手机为 {} 的 apikey：{}", phone, apiKey);
            }
            return UserMsg.REGISTERSUCCESS.getCode();
        } catch (SuperBootException e) {
            logger.error("获取渠道信息异常", e.getMessage());
            throw new SuperBootException("lhq-superboot-user-0011");
        } catch (Exception e) {
            logger.error("执行语句异常", e);
            throw new SuperBootException(e);
        }

    }

    @Override
    public User getUserByWechatId(String wechatId) {
        if (StringUtils.isEmpty(wechatId)) {
            logger.error("微信Id为空");
            return null;
        }

        UserExample userExample = new UserExample();
        userExample.createCriteria().andWechatIdEqualTo(wechatId).andIsEnabledEqualTo(IS_ENABLED.YES.value())
                .andIsDeletedEqualTo(IS_DELETE.NO.value());
        List<User> userList = userMapper.selectByExample(userExample);

        if (userList.isEmpty()) {
            logger.info("暂无该用户,wechatId: {} ", wechatId);
            return null;
        }
        if (userList.size() > 1) {
            logger.error("用户中存在多个用户的wechatId相同,wechatId: {}", wechatId);
            throw new SuperBootException("lhq-superboot-user-0011");
        }

        return userList.get(0);
    }

    @Override
    public boolean checkUsername(String userName, String channelFlg) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUserNameEqualTo(userName)
                .andChannelIdEqualTo(getChannelMsg(channelFlg).getChannelId());
        return !userMapper.selectByExample(userExample).isEmpty();
    }

    @Override
    public boolean checkPhone(String phone, String channelFlg) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andPhoneEqualTo(phone)
                .andChannelIdEqualTo(getChannelMsg(channelFlg).getChannelId());
        return !userMapper.selectByExample(userExample).isEmpty();
    }

    @Override
    @Transactional
    public boolean updatePwdByPhone(String phone, String password, String channelFlg) {
        User user = new User().toBuilder().password(ShiroMd5Util.PwdMd5(password)).build();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andPhoneEqualTo(phone)
                .andChannelIdEqualTo(getChannelMsg(channelFlg).getChannelId());

        List<User> userList = userMapper.selectByExample(userExample);
        if (userList.isEmpty()) {
            logger.info("不存在手机号为{}的用户 ", phone);
            throw new SuperBootException("lhq-superboot-phone-0011", phone);
        }
        if (userList.size() >= 2) {
            logger.info("手机号为{}的用户存在两个 ", phone);
            throw new SuperBootException("lhq-superboot-phone-0012", phone);
        }
        userMapper.updateByExampleSelective(user, userExample);

        // 清除缓存数据
        Cache<Object, AuthenticationInfo> authenticationCache = localRealm.getAuthenticationCache();
        if (authenticationCache != null) {
            authenticationCache.remove(userList.get(0).getUserId());
        }

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getConsumerIdByPhone(String phone, String userRealName) {
        if (!CheckUtils.isPhoneLegal(phone)) {
            logger.error("手机号{}不合法", phone);
            throw new SuperBootException("lhq-superboot-phone-0007");
        }

        UserConsumer user = userRepository.selectWechatUserByPhone(phone);
        if (user != null) {
            if (StringUtils.isEmpty(user.getConsumer().getConsumerId())) {
                logger.error("手机号{}的用户数据在consumer表中无法找到", phone);
                throw new SuperBootException("lhq-superboot-phone-0010", phone);
            }
            return user.getConsumer().getConsumerId();
        }

        Channel channel = getChannelMsg(LoginSource.XCX.name());
        try {
            // 插入用户信息表
            UserInfo userInfo = new UserInfo().toBuilder().userRealName(userRealName).build();
            userInfoMapper.insertSelective(userInfo);
            String userInfoId = userInfo.getUserInfoId();

            // 插入用户表
            User newUser = new User().toBuilder().phone(phone).channelId(channel.getChannelId()).userInfoId(userInfoId)
                    .build();
            userMapper.insertSelective(newUser);

            // 插入一条顾客信息进顾客表
            Consumer consumer = new Consumer().toBuilder().userId(newUser.getUserId()).build();
            consumerMapper.insertSelective(consumer);

            return consumer.getConsumerId();
        } catch (Exception e) {
            logger.error("执行语句异常", e);
            throw new SuperBootException("lhq-superboot-user-0020");
        }
    }

    @Override
    public boolean updatePhoneByCurrentUser(String phone) {
        String userId = getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return false;
        }
        User user = new User().toBuilder().phone(phone).build();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUserIdEqualTo(userId);
        userMapper.updateByExampleSelective(user, userExample);
        return true;
    }

    @Override
    public List<UserManager> getManagerList(List<String> managerIdList) {
        if (managerIdList == null || managerIdList.isEmpty()) {
            return null;
        }

        return userRepository.selectManagerList(managerIdList);
    }

    @Override
    public boolean checkUserRole(List<String> userList) {

        ManagerExample managerExample = new ManagerExample();
        managerExample.createCriteria().andUserIdIn(userList).andRoleIdEqualTo(DEFAULT_HT_MANAGER_ROLE_ID);

        List<Manager> managerList = managerMapper.selectByExample(managerExample);
        return managerList.isEmpty();
    }

    @Override
    public void disableUser(List<String> userIdList, Integer isEnabled) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUserIdIn(userIdList);
        User user = new User().toBuilder().isEnabled(isEnabled).build();
        userMapper.updateByExampleSelective(user, userExample);
    }

    @Override
    public void deleteUser(List<String> userIdList) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUserIdIn(userIdList);
        User user = new User().toBuilder().isDeleted(IS_DELETE.YES.value()).build();
        userMapper.updateByExampleSelective(user, userExample);
    }

    @SuppressWarnings("unused")
    private List<Channel> getChannelMsg() {
        List<String> sourceList = new ArrayList<>(2);
        sourceList.add(LoginSource.PC.name());
        sourceList.add(LoginSource.HT.name());

        ChannelExample channelExample = new ChannelExample();
        channelExample.createCriteria().andChannelFlgIn(sourceList);
        List<Channel> channel = channelMapper.selectByExample(channelExample);
        if (channel.isEmpty()) {
            logger.info("无法获取pc和后台的渠道信息 ");
            throw new SuperBootException("lhq-superboot-user-0011");
        }
        return channel;
    }

    private Channel getChannelMsg(String channelFlg) {
        ChannelExample channelExample = new ChannelExample();
        channelExample.createCriteria().andChannelFlgEqualTo(channelFlg);
        List<Channel> channel = channelMapper.selectByExample(channelExample);
        if (channel.isEmpty()) {
            logger.info("无法获取{}的渠道信息 ", channelFlg);
            throw new SuperBootException("lhq-superboot-user-0011");
        }
        return channel.get(0);
    }

}
