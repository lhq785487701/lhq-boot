package com.lhq.superboot.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.RechargeService;
import com.lhq.superboot.util.BigDecimalUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.RechargeVo;
import com.lhq.superboot.wechat.WechatPayApi;

import io.swagger.annotations.Api;

/**
 * @Description: 用户充值
 * @author: lihaoqi
 * @date: 2019年7月9日 上午12:31:05
 * @version: v1.0.0
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user/recharge")
@Api(value = "用户微信充值接口controller", tags = {"用户微信充值操作接口"})
public class RechargeController {

    private static final Logger logger = LoggerFactory.getLogger(RechargeController.class);

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private WechatPayApi wechatPayApi;

    /**
     * @return
     * @Description: 用户充值下单
     */
    @PostMapping("/order")
    public Object userRechargeOrder(@RequestBody RechargeVo rechargeVo) {
        Long token = BigDecimalUtils.toLong(rechargeVo.getToken());
        Long money = BigDecimalUtils.toLong(rechargeVo.getMoney());

        if (token <= 0) {
            throw new SuperBootException("lhq-superboot-recharge-0002");
        }
        if (money <= 0) {
            throw new SuperBootException("lhq-superboot-recharge-0001");
        }
        return rechargeService.rechargeOrder(rechargeVo);
    }

    /**
     * @return
     * @Description: 用户充值通知(该方法不自动返回result格式 ， 返回xml格式)
     */
    @PostMapping("/notify")
    public Object userRechargeNotify() {
        return rechargeService.rechargeNotify();
    }

    @GetMapping("/query")
    public Object queryRechargeOrder(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new SuperBootException("lhq-superboot-recharge-0006");
        }

        try {
            return wechatPayApi.queryOrder(orderNo).getDesc();
        } catch (Exception e) {
            logger.error("msg", e);
        }
        return null;
    }
}
