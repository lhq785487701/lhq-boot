package com.lhq.superboot.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lhq.superboot.domain.SMSSendDetail;
import com.lhq.superboot.domain.SMSSendDetailExample;
import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.domain.SMSSendLogExample;
import com.lhq.superboot.enums.AliSMSSendCode;
import com.lhq.superboot.enums.SMSSendState;
import com.lhq.superboot.mapper.SMSSendDetailMapper;
import com.lhq.superboot.mapper.SMSSendLogMapper;
import com.lhq.superboot.service.SMSLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 短信日志实现类
 * @author: lihaoqi
 * @date: 2019年8月19日 下午10:03:27
 * @version: v1.0.0
 */
@Service
public class SMSLogServiceImpl implements SMSLogService {

    private static final Logger logger = LoggerFactory.getLogger(SMSLogService.class);

    @Autowired
    private SMSSendLogMapper smsSendLogMapper;

    @Autowired
    private SMSSendDetailMapper smsSendDetailMapper;

    @Override
    public Page<SMSSendLog> querySendLog(String userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        SMSSendLogExample example = new SMSSendLogExample();
        SMSSendLogExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isEmpty(userId)) {
            criteria.andUserIdLike("%" + userId + "%");
        }
        return (Page<SMSSendLog>) smsSendLogMapper.selectByExample(example);
    }

    @Override
    public List<SMSSendLog> querySendLogIsWait() {
        SMSSendLogExample example = new SMSSendLogExample();
        example.createCriteria().andStatusEqualTo(SMSSendState.WAIT.getCode())
                .andRequestCodeEqualTo(AliSMSSendCode.OK.getCode());

        return smsSendLogMapper.selectByExample(example);
    }

    @Override
    public SMSSendLog querySendLogById(Integer sendLogId) {
        return smsSendLogMapper.selectByPrimaryKey(sendLogId);
    }

    @Override
    public void createSendLog(SMSSendLog smsSendLog) {
        try {
            smsSendLogMapper.insertSelective(smsSendLog);
        } catch (Exception e) {
            logger.error("插入短信日志失败，异常信息：{}", e.getMessage());
        }
    }

    @Override
    public void updateSendLog(SMSSendLog smsSendLog) {
        try {
            smsSendLogMapper.updateByPrimaryKeySelective(smsSendLog);
        } catch (Exception e) {
            logger.error("更新短信日志失败，异常信息：{}", e.getMessage());
        }

    }

    @Override
    public Page<SMSSendDetail> querySendDetailByPage(Integer sendLogId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        SMSSendDetailExample example = new SMSSendDetailExample();
        example.createCriteria().andSmsSendLogIdEqualTo(sendLogId);

        return (Page<SMSSendDetail>) smsSendDetailMapper.selectByExample(example);
    }

    @Override
    public List<SMSSendDetail> querySendDetailById(Integer sendLogId) {
        SMSSendDetailExample example = new SMSSendDetailExample();
        example.createCriteria().andSmsSendLogIdEqualTo(sendLogId);

        return smsSendDetailMapper.selectByExample(example);
    }

    @Override
    public void createSendDetail(SMSSendDetail smsSendDetail) {
        try {
            smsSendDetailMapper.insertSelective(smsSendDetail);
        } catch (Exception e) {
            logger.error("插入短信详情失败，异常信息：{}", e.getMessage());
        }
    }

    @Override
    public void updateSendDetail(SMSSendDetail smsSendDetail) {
        try {
            smsSendDetailMapper.updateByPrimaryKeySelective(smsSendDetail);
        } catch (Exception e) {
            logger.error("更新短信详情失败，异常信息：{}", e.getMessage());
        }
    }

}
