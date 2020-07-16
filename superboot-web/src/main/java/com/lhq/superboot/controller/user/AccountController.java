package com.lhq.superboot.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.service.AccountService;
import com.lhq.superboot.vo.PayLogVo;

import io.swagger.annotations.Api;

/**
 * @Description: 用户账户相关，如金额、token流水等
 * @author: lihaoqi
 * @date: 2019年7月10日 上午11:37:42
 * @version: v1.0.0
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user/account")
@Api(value = "用户账号controller", tags = {"用户账号操作接口"})
public class AccountController {

    @Autowired
    private AccountService accountService;


    /**
     * @param page
     * @return
     * @Description: 分页获取用户的支付金额流水日志
     */
    @GetMapping("/pay/log/list")
    public Object getUserPayLog(PageInfo<Object> page) {
        List<PayLog> payLog = accountService.queryUserPayLog(page.getPageNum(), page.getPageSize());
        if (payLog.isEmpty()) {
            return new ArrayList<PayLogVo>();
        }
        return payLog.stream().map(log -> PayLogVo.convert(log)).collect(Collectors.toList());
    }

    /**
     * @param page
     * @return
     * @Description: 分页获取用户的微信下单日志(微信支付时的下单记录)
     */
    @GetMapping("/wxorder/log/list")
    public Object getUserWxorderLog(PageInfo<Object> page) {
        return accountService.queryUserWxOrderLog(page.getPageNum(), page.getPageSize());
    }
}
