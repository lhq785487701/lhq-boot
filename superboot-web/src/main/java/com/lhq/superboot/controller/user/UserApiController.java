package com.lhq.superboot.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.UserApiService;
import com.lhq.superboot.util.StringUtils;

import io.swagger.annotations.Api;

/**
 * @Description: 用户对外接口时的信息获取(不是对外接口)
 * @author: lihaoqi
 * @date: 2019年7月24日 上午11:40:56
 * @version: v1.0.0
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user/api")
@Api(value = "用户对外接口controller", tags = {"用户对外接口操作接口"})
public class UserApiController {

    @Autowired
    private UserApiService userApiService;

    /**
     * @return
     * @Description: 获取当前用户key
     */
    @GetMapping("/getKey")
    public Object getKey() {
        return userApiService.getApiKey();
    }

    /**
     * @return
     * @Description: 获取用户secret
     */
    @GetMapping("/getSecret")
    public Object getSecret(String appKey) {
        if (StringUtils.isEmpty(appKey)) {
            throw new SuperBootException("lhq-superboot-user-0016");
        }
        return userApiService.getApiSecret(appKey);
    }

}
