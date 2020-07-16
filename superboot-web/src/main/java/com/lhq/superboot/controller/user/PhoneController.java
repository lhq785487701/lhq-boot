package com.lhq.superboot.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhq.superboot.enums.LoginSource;
import com.lhq.superboot.enums.UserMsg;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.qo.PhoneCodeQo;
import com.lhq.superboot.service.PhoneService;
import com.lhq.superboot.service.SMSCodeService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.CheckUtils;
import com.lhq.superboot.util.StringUtils;

import io.swagger.annotations.Api;

/**
 * @Description: 手机模块，主要验证码
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
@Api(value = "手机接口controller", tags = {"手机操作接口"})
public class PhoneController {

    @Autowired
    private UserService userService;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private SMSCodeService smsCodeService;

    /**
     * @param phoneCodeQo
     * @return
     * @Description 获取手机验证码
     */
    @PostMapping("/getCode")
    public Object getPhoneValidCode(@RequestBody PhoneCodeQo phoneCodeQo) {
        String phone = phoneCodeQo.getPhone();
        if (StringUtils.isEmpty(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        if (!CheckUtils.isPhoneLegal(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0007");
        }
        if (!smsCodeService.validateCodeRepeatSend(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0013");
        }

        smsCodeService.getSMSCode(phone);
        return UserMsg.GETCODESUCCESS.getCode();
    }

    /**
     * @param phoneCodeQo
     * @return
     * @Description 绑定手机号
     */
    @PostMapping("/bindingPhone")
    public Object bindingPhone(@RequestBody PhoneCodeQo phoneCodeQo) {
        String phone = phoneCodeQo.getPhone();
        String code = phoneCodeQo.getCode();
        if (StringUtils.isEmpty(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        if (userService.checkPhone(phone, LoginSource.HT.name())) {
            throw new SuperBootException("lhq-superboot-phone-0005");
        }
        if (!smsCodeService.validateCode(phone, code)) {
            throw new SuperBootException("lhq-superboot-phone-0003");
        }
        if (!userService.updatePhoneByCurrentUser(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0003");
        }

        return UserMsg.BINDPHONESUCCESS.getCode();
    }

    /**
     * @param phoneCodeQo
     * @return
     * @Description 忘记密码-获取手机验证码
     */
    @PostMapping("/getCodeForFp")
    public Object getCodeForFp(@RequestBody PhoneCodeQo phoneCodeQo) {
        String phone = phoneCodeQo.getPhone();
        if (StringUtils.isEmpty(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        // 校验该手机号是否已经注册到pc
        if (!userService.checkPhone(phone, LoginSource.PC.name())) {
            throw new SuperBootException("lhq-superboot-phone-0009");
        }

        smsCodeService.getSMSCode(phone);
        return UserMsg.GETCODESUCCESS.getCode();
    }

    /**
     * @param phoneCodeQo
     * @return
     * @Description: 忘记密码-验证手机验证码
     */
    @PostMapping("/validateCodeForFp")
    public Object validateCodeForFp(@RequestBody PhoneCodeQo phoneCodeQo) {
        String phone = phoneCodeQo.getPhone();
        String code = phoneCodeQo.getCode();
        if (StringUtils.isEmpty(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        // 校验短信验证码是否正确
        if (!smsCodeService.validateCode(phone, code)) {
            throw new SuperBootException("lhq-superboot-phone-0006");
        }

        return phoneService.setPhoneRandomInCache(phone);
    }

}
