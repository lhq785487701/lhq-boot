package com.lhq.superboot.api.service.impl;

import com.lhq.superboot.api.service.ApiLogService;
import com.lhq.superboot.domain.SysApiLog;
import com.lhq.superboot.mapper.SysApiLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 写入api日志
 * @author: lihaoqi
 * @date: 2019/12/23 15:00
 * @version: v1.0.0
 */
@Service
public class ApiLogServiceImpl implements ApiLogService {

    private static final Logger logger = LoggerFactory.getLogger(ApiLogService.class);

    @Autowired
    private SysApiLogMapper apiLogMapper;

    @Override
    public void createLog(String apikey, String result, String params, String message, String method, int isasyn) {
        try {
            logger.info("[ApiLogService] 修改执行表状态与写入日志");
            SysApiLog sysApiLog = new SysApiLog().toBuilder().method(method).message(message)
                    .params(params).result(result)
                    .isasyn(isasyn).apikey(apikey).build();
            apiLogMapper.insertSelective(sysApiLog);
        } catch (Exception e) {
            logger.error("[ApiLogService] -> 插入执行表失败：{}", e);
        }
    }

}
