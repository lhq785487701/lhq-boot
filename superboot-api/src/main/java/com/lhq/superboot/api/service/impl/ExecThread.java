package com.lhq.superboot.api.service.impl;

import com.lhq.superboot.api.domain.ApiResult;
import com.lhq.superboot.api.enums.AsynType;
import com.lhq.superboot.api.service.ApiLogService;
import com.lhq.superboot.api.utils.ApiReflectUtils;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.OkHttpUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 执行方法的线程(异步回执情况下)
 * @author: lihaoqi
 * @date: 2019/12/23 23:14
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ExecThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecThread.class);

    private String apiKey;
    private String className;
    private String methodName;
    private List<String> paramTypeList;
    private List<Object> paramValueList;
    private String rtnUrl;

    @Resource(name = "apiReflect")
    private ApiReflectUtils apiReflect;

    @Autowired
    private ApiLogService apiLogService;

    @Override
    public void run() {
        String method = className + "." + methodName;

        ApiResult apiResult = apiReflect.invoke(className, methodName, paramTypeList, paramValueList);
        apiLogService.createLog(apiKey, apiResult.getResult(), FastJsonUtils.toJSONFeatures(paramValueList), apiResult.getMessage(), method, AsynType.ASYN.code());
        logger.info("返回信息：{}, 回调地址：{}", apiResult, rtnUrl);

        // 接收者通过流的方式获取json字符串
        String result = OkHttpUtils.postJsonParams(rtnUrl, FastJsonUtils.toJSONFeatures(apiResult));
        logger.debug("[ExecThread] -> 异步回执响应:{}", result);
    }
}
