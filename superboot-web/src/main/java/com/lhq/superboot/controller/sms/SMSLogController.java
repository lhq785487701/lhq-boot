package com.lhq.superboot.controller.sms;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.SMSSendDetail;
import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.SMSLogService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.SMSSendDetailVo;
import com.lhq.superboot.vo.SMSSendLogVo;

import io.swagger.annotations.Api;

/**
 * @author 作者 lihaoqi
 * @version 创建时间：2019年6月11日 下午1:43:38
 * @Description: 用户短信日志
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/sms")
@Api(value = "短信日志controller", tags = {"短信日志操作接口"})
public class SMSLogController {

    @Autowired
    private SMSLogService smsLogService;

    @Autowired
    private UserService userService;

    /**
     * 分页查询短信日志列表(后台查询日志功能)
     *
     * @param pageInfo
     * @return
     */
    @GetMapping("/log/ht/list")
    public Object getSMSSendLog(String userId, PageInfo<SMSSendLogVo> pageInfo) {
        Page<SMSSendLog> smsLogPage = smsLogService.querySendLog(userId, pageInfo.getPageNum(), pageInfo.getPageSize());

        pageInfo.setList(smsLogPage.getResult().stream().map(s -> SMSSendLogVo.convert(s)).collect(Collectors.toList()));
        pageInfo.setTotal(smsLogPage.getTotal());
        return pageInfo;
    }

    /**
     * 分页查询短信日志列表(pc查询日志功能)
     *
     * @param pageInfo
     * @return
     */
    @GetMapping("/log/pc/list")
    public Object getUserSMSSendLog(PageInfo<SMSSendLogVo> pageInfo) {
        String userId = userService.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        Page<SMSSendLog> smsLogPage = smsLogService.querySendLog(userId, pageInfo.getPageNum(), pageInfo.getPageSize());

        pageInfo.setList(smsLogPage.getResult().stream().map(s -> SMSSendLogVo.convert(s)).collect(Collectors.toList()));
        pageInfo.setTotal(smsLogPage.getTotal());
        return pageInfo;
    }

    /**
     * 查看日志详情
     *
     * @param pageInfo
     * @return
     */
    @GetMapping("/log/detail/list")
    public Object getSMSSendDetail(Integer sendLogId, PageInfo<SMSSendDetailVo> pageInfo) {
        Page<SMSSendDetail> smsDetailPage = smsLogService.querySendDetailByPage(sendLogId, pageInfo.getPageNum(), pageInfo.getPageSize());

        pageInfo.setList(smsDetailPage.getResult().stream().map(s -> SMSSendDetailVo.convert(s)).collect(Collectors.toList()));
        pageInfo.setTotal(smsDetailPage.getTotal());
        return pageInfo;
    }

}
