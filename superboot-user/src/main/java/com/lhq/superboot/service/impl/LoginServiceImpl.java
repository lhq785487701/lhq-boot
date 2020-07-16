package com.lhq.superboot.service.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.Manager;
import com.lhq.superboot.domain.UserConsumer;
import com.lhq.superboot.domain.UserRole;
import com.lhq.superboot.enums.LoginMsg;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.LoginService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.shiro.LocalWechatToken;
import com.lhq.superboot.shiro.property.ShiroConfigProperty;
import com.lhq.superboot.util.ExceptionUtils;
import com.lhq.superboot.util.ResultUtils;
import com.lhq.superboot.vo.PcLoginVo;

/**
 * @Description: login impl
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ShiroConfigProperty shiroProp;

    @Override
    public String userAccountLogin(PcLoginVo pcLoginVo) {
        String password = pcLoginVo.getPassword();
        String userName = pcLoginVo.getUserName();
        Subject subject = null;

        try {
            // 通过用户名与渠道为px或者ht获取用户信息
            UserRole user = userService.getUserByUsername(pcLoginVo);

            if (user == null) {
                logger.info("不存在该用户：{} ", userName);
                throw new UnknownAccountException();
            }
            if (user.getPassword() == null) {
                logger.info("用户密码为空：{}", userName);
                throw new IncorrectCredentialsException();
            }
            if (user.getIsEnabled() != 1) {
                logger.info("用户已禁用：{}", userName);
                throw new DisabledAccountException();
            }

            subject = SecurityUtils.getSubject();
            // 由于pc后台与小程序用户验证方式不同，所以使用userid
            LocalWechatToken localToken = new LocalWechatToken(user.getUserId(), password, user.getPassword());
            subject.login(localToken);
        } catch (UnknownAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.UNKNOWNACCOUNT.getCode(), e);
        } catch (IncorrectCredentialsException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.INCORRECTPWD.getCode(), e);
        } catch (DisabledAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.DISABLED.getCode(), e);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e);
        } catch (Throwable e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), ExceptionUtils.getExceptionMsg(e));
        }
        if (subject.getSession(false).getId() == null) {
            ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.NOSESSIONID.getCode());
        }
        return ResultUtils.successWithSessionId(LoginMsg.LOGINSUCCESS.getCode(), subject.getSession(false).getId());
    }

    @Override
    public String userPhoneLogin(String phone, String channelFlg) {
        Subject subject = null;

        try {
            Manager user = userService.getManagerByPhone(phone, channelFlg);

            if (user == null) {
                logger.info("不存在该用户或禁用,手机号：{} ", phone);
                throw new UnknownAccountException();
            }

            subject = SecurityUtils.getSubject();
            // 免密登录
            LocalWechatToken localToken = new LocalWechatToken(user.getUserId());
            subject.login(localToken);
        } catch (UnknownAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.UNKNOWNACCOUNT.getCode(), e);
        } catch (DisabledAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.DISABLED.getCode(), e);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e);
        } catch (Throwable e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), ExceptionUtils.getExceptionMsg(e));
        }
        if (subject.getSession(false).getId() == null) {
            ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.NOSESSIONID.getCode());
        }
        return ResultUtils.successWithSessionId(LoginMsg.LOGINSUCCESS.getCode(), subject.getSession(false).getId());
    }

    @Override
    public String wxLogin(String phone) {
        Subject subject = null;
        try {
            // 通过手机号与渠道为xcx获取用户
            UserConsumer user = userService.getWechatUserByPhone(phone);

            if (user == null) {
                logger.info("不存在手机为:{}的小程序用户", phone);
                throw new UnknownAccountException();
            }
            if (user.getUser().getIsEnabled() != 1) {
                logger.info("微信用户已禁用：{}", phone);
                throw new DisabledAccountException();
            }

            subject = SecurityUtils.getSubject();
            LocalWechatToken localToken = new LocalWechatToken(user.getUser().getUserId());
            subject.login(localToken);
        } catch (UnknownAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.UNKNOWNACCOUNT.getCode(), e);
        } catch (DisabledAccountException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.DISABLED.getCode(), e);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e);
        } catch (Throwable e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), ExceptionUtils.getExceptionMsg(e));
        }
        if (subject.getSession(false).getId() == null) {
            ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.NOSESSIONID.getCode());
        }
        return ResultUtils.success(shiroProp.getSessionIdName() + "=" + subject.getSession(false).getId(),
                LoginMsg.LOGINSUCCESS.getCode());
    }

}
