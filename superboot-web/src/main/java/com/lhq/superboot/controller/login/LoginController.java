package com.lhq.superboot.controller.login;

import com.lhq.superboot.domain.Result;
import com.lhq.superboot.domain.WechatAuthResult;
import com.lhq.superboot.enums.LoginMode;
import com.lhq.superboot.enums.LoginMsg;
import com.lhq.superboot.enums.RegisterMsg;
import com.lhq.superboot.enums.WechatErrCode;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.AppService;
import com.lhq.superboot.service.LoginService;
import com.lhq.superboot.service.SMSCodeService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.ResultUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.PcLoginVo;
import com.lhq.superboot.vo.WechatLoginVo;
import com.lhq.superboot.wechat.impl.WechatBaseApiService;
import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

/**
 * @Description: 用户登录controller 不被切面处理，需自行处理返回json结果集
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@Api(value = "登录controller", tags = {"登录操作接口"})
public class LoginController {

    /**
     * 小程序storage切割#后,数组长度
     **/
    private static final int WECHAT_SPLIT_LEN = 3;

    /**
     * 微信storage切割#后,数组长度
     **/
    private static final int APP_SPLIT_LEN = 2;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private WechatBaseApiService wechatApi;

    @Autowired
    private SMSCodeService smsCodeService;

    @Autowired
    private AppService appService;

    /**
     * @param pcLoginVo
     * @return
     * @Description PC HT登录
     */
    @PostMapping(value = "/user/login")
    public String login(@RequestBody PcLoginVo pcLoginVo) {
        String loginMode = pcLoginVo.getLoginMode();
        if (StringUtils.isEmpty(loginMode) || LoginMode.ACCOUNT_PWD.getCode().equals(loginMode)) {
            return accountLogin(pcLoginVo);
        } else if (StringUtils.isEmpty(loginMode) || LoginMode.PHONE_CODE.getCode().equals(loginMode)) {
            return phoneCodeLogin(pcLoginVo);
        } else {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.UNKNOWNMODE.getCode());
        }
    }

    /**
     * @param pcLoginVo
     * @return
     * @Description: 手机验证码登录
     */
    private String phoneCodeLogin(PcLoginVo pcLoginVo) {
        String phone = pcLoginVo.getPhone();
        String validCode = pcLoginVo.getValidCode();
        String channelFlg = pcLoginVo.getChannelFlg();

        if (StringUtils.isEmpty(phone)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONEEMPTY.getCode());
        }
        if (StringUtils.isEmpty(validCode)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONECODEEMPTY.getCode());
        }
        try {
            smsCodeService.validateCode(phone, validCode);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONECODEERROR.getCode());
        }
        return loginService.userPhoneLogin(phone, channelFlg);
    }

    /**
     * @param pcLoginVo
     * @return
     * @Description: 账号密码登录
     */
    private String accountLogin(PcLoginVo pcLoginVo) {
        if (StringUtils.isEmpty(pcLoginVo.getUserName())) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.EMPTYACCOUNT.getCode());
        }
        if (StringUtils.isEmpty(pcLoginVo.getPassword())) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.EMPTYPASSWORD.getCode());
        }
        if (StringUtils.isEmpty(pcLoginVo.getChannelFlg())) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.UNKNOWNSOURCE.getCode());
        }
        return loginService.userAccountLogin(pcLoginVo);
    }


    /**
     * @param wechatQo
     * @return
     * @Description 微信注册
     */
    @PostMapping("/user/wxchat/register")
    public Object wechatRegister(@RequestBody WechatLoginVo wechatQo) {
        String phone = wechatQo.getPhone();
        String code = wechatQo.getCode();
        String validCode = wechatQo.getValidCode();
        // 判断phone是否为空
        if (StringUtils.isEmpty(phone)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONEEMPTY.getCode());
        }
        // 判断code是否为空
        if (StringUtils.isEmpty(code)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.WECHATCODE.getCode());
        }

        // 首先验证手机验证码
        try {
            smsCodeService.validateCode(phone, validCode);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONECODEERROR.getCode());
        }

        // 通过code获取用户wechat_id
        WechatAuthResult wechatAuthResult = wechatApi.getUserOpenid(code);
        if (null == wechatAuthResult) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value());
        }
        // 判断微信状态
        if (wechatAuthResult.getErrcode() != WechatErrCode.SUCCESS.getCode()) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), wechatAuthResult.getErrmsg());
        }
        String wechatId = wechatAuthResult.getOpenid();
        String seesionKey = wechatAuthResult.getSessionKey();

        // 将手机号注册
        if (!userService.registerWechatUser(phone, wechatId)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), RegisterMsg.WECHATREGISTERERROR.getCode());
        }

        // 登录
        String resultJson = loginService.wxLogin(phone);
        Result<?> result = (Result<?>) FastJsonUtils.convertJsonToObject(resultJson, Result.class);
        if (result.getCode() != HttpStatus.OK.value()) {
            return resultJson;
        }
        String token = wechatId + "#" + seesionKey + "#" + phone;
        return ResultUtils.success(Base64.getEncoder().encodeToString(token.getBytes()),
                LoginMsg.LOGINSUCCESS.getCode());
    }

    /**
     * @param wechatVo
     * @return
     * @Description 微信登录
     */
    @PostMapping("/user/wxchat/login")
    public Object wechatLogin(@RequestBody WechatLoginVo wechatVo) {
        String token = wechatVo.getToken();
        if (StringUtils.isEmpty(token)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.WECHATTOKENEMPTY.getCode());
        }
        token = new String(Base64.getDecoder().decode(token));
        String[] tokenMsg = token.split("#");
        if (tokenMsg.length != WECHAT_SPLIT_LEN) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.TOKENSPLITERROR.getCode());
        }
        String phone = tokenMsg[2];
        return loginService.wxLogin(phone);
    }

    /**
     * @param wechatVo
     * @return
     * @Description app注册
     */
    @PostMapping("/user/app/register")
    public Object appRegister(@RequestBody WechatLoginVo wechatVo) {
        String phone = wechatVo.getPhone();
        String validCode = wechatVo.getValidCode();
        // 判断phone是否为空
        if (StringUtils.isEmpty(phone)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONEEMPTY.getCode());
        }

        // 首先验证手机验证码
        try {
            smsCodeService.validateCode(phone, validCode);
        } catch (SuperBootException e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.PHONECODEERROR.getCode());
        }

        // 生成一个sessionKey,并存入redis，作为保存用户登录状态的session
        String seesionKey = appService.setSessionKey(phone);

        // 将手机号注册
        if (!userService.registerAppUser(phone)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), RegisterMsg.APPREGISTERERRO.getCode());
        }

        // 登录
        String resultJson = loginService.wxLogin(phone);
        Result<?> result = (Result<?>) FastJsonUtils.convertJsonToObject(resultJson, Result.class);
        if (result.getCode() != HttpStatus.OK.value()) {
            return resultJson;
        }
        String token = seesionKey + "#" + phone;
        return ResultUtils.success(Base64.getEncoder().encodeToString(token.getBytes()),
                LoginMsg.LOGINSUCCESS.getCode());
    }

    /**
     * @param wechatQo
     * @return
     * @Description app登录
     */
    @PostMapping("/user/app/login")
    public Object appLogin(@RequestBody WechatLoginVo wechatQo) {
        String token = wechatQo.getToken();
        if (StringUtils.isEmpty(token)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.APPTOKENEMPTY.getCode());
        }
        token = new String(Base64.getDecoder().decode(token));
        String[] tokenMsg = token.split("#");
        if (tokenMsg.length != APP_SPLIT_LEN) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.TOKENSPLITERROR.getCode());
        }
        String phone = tokenMsg[1];
        return loginService.wxLogin(phone);
    }

    /**
     * @param wechatQo
     * @return
     * @Description 判断session是否还生效
     */
    @PostMapping("/user/app/judgeSession")
    public Object appJudgeSession(@RequestBody WechatLoginVo wechatQo) {
        String token = wechatQo.getToken();
        if (StringUtils.isEmpty(token)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.APPTOKENEMPTY.getCode());
        }
        token = new String(Base64.getDecoder().decode(token));
        String[] tokenMsg = token.split("#");
        if (tokenMsg.length != APP_SPLIT_LEN) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.TOKENSPLITERROR.getCode());
        }
        String sessionKey = tokenMsg[0];
        String phone = tokenMsg[1];
        if (!appService.isExpireSessionKey(phone, sessionKey)) {
            return ResultUtils.error(HttpStatus.BAD_REQUEST.value(), LoginMsg.APPSESSIONEXPIRE.getCode());
        }
        return ResultUtils.success(HttpStatus.OK.name());
    }

    /**
     * @Description: 登出
     */
    @GetMapping(value = "/user/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResultUtils.error(HttpStatus.OK.value(), LoginMsg.LOGOUTSUCCESS.getCode());
    }

    /**
     * @Description: 未授权（废弃）
     */
    @GetMapping(value = "/unauthorized")
    public String unauthorized() {
        return ResultUtils.error(HttpStatus.FORBIDDEN.value(), LoginMsg.UNAUTH.getCode());
    }

    /**
     * @Description: 未登录（废弃）
     */
    @GetMapping(value = "/unlogin")
    public String unlogin() {
        return ResultUtils.error(HttpStatus.UNAUTHORIZED.value(), LoginMsg.UNLOGIN.getCode());
    }

}
