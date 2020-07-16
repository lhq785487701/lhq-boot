package com.lhq.superboot.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lhq.superboot.api.domain.ApiResult;
import com.lhq.superboot.api.enums.AsynType;
import com.lhq.superboot.api.enums.ExecResult;
import com.lhq.superboot.api.enums.FormatType;
import com.lhq.superboot.api.service.ApiExecService;
import com.lhq.superboot.api.utils.ApiReflectUtils;
import com.lhq.superboot.domain.SysApiExecute;
import com.lhq.superboot.domain.SysApiExecuteExample;
import com.lhq.superboot.enums.ConstEnumUtils;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.SysApiExecuteMapper;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Description: api执行接口
 * @author: lihaoqi
 * @date: 2019/12/23 15:00
 * @version: v1.0.0
 */
@Service
public class ApiExecServiceImpl implements ApiExecService {

    private static final Logger logger = LoggerFactory.getLogger(ApiExecService.class);

    @Value("${api.cacheKey}")
    private String cacheKey;

    @Value("${api.cacheTime}")
    private long cacheTime;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysApiExecuteMapper apiExecMapper;

    @Resource(name = "apiReflect")
    private ApiReflectUtils apiReflect;

    @Autowired
    private ExecThread execThread;

    @Override
    public SysApiExecute getApiExceByCode(String execCode) {
        Object execCache = redisUtils.get(cacheKey + execCode);
        if (execCache != null) {
            return FastJsonUtils.toBean((String) execCache, SysApiExecute.class);
        }

        SysApiExecuteExample param = new SysApiExecuteExample();
        param.createCriteria()
                .andExecCodeEqualTo(execCode)
                .andIsDeletedEqualTo(ConstEnumUtils.IS_DELETE.NO.value())
                .andIsEnabledEqualTo(ConstEnumUtils.IS_ENABLED.YES.value());
        List<SysApiExecute> sysApiList = apiExecMapper.selectByExample(param);
        if (sysApiList == null) {
            return null;
        } else {
            SysApiExecute sysApiExecute = sysApiList.get(0);
            redisUtils.set(cacheKey + execCode, FastJsonUtils.toJSONFeatures(sysApiExecute), cacheTime);
            return sysApiExecute;
        }
    }

    @Override
    public ApiResult exce(String apiKey, String className, String methodName, String paramType,
                          String params, String format, int isAsyn, String rtnUrl) {
        logger.info("[ApiExecService] -> 正在处理api请求，执行方法：{}，执行类路径：{}，执行参数：{}", methodName, className, paramType);
        List<String> paramTypeList = new ArrayList<>();
        List<Object> paramValueList = new ArrayList<>();

        if (StringUtils.isEmpty(paramType)) {
            // 执行方法的形参为空时
            if (StringUtils.isNotEmpty(params)) {
                logger.error("[ApiExecService] -> 形参为空，参数不为空，参数：{}", params);
                throw new SuperBootException("lhq-superboot-api-0020", params);
            }
        } else {
            if (params == null) {
                logger.error("[ApiExecService] -> 形参不为空，参数为空，形参：{}", paramType);
                throw new SuperBootException("lhq-superboot-api-0021", paramType);
            }
            LinkedHashMap<String, String> paramTypeMap = FastJsonUtils.stringToLinkedHashMap(paramType);
            Map<String, Object> paramTypeValue = changeParams(params, format);
            checkAndGetValue(paramTypeMap, paramTypeValue, paramTypeList, paramValueList);
        }

        // 异步与非异步
        if (isAsyn == AsynType.NOT_ASYN.code()) {
            return apiReflect.invoke(className, methodName, paramTypeList, paramValueList);
        } else if (isAsyn == AsynType.ASYN.code()) {
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
            fixedThreadPool.execute(execThread.toBuilder().apiKey(apiKey).className(className).methodName(methodName)
                    .paramTypeList(paramTypeList).paramValueList(paramValueList).rtnUrl(rtnUrl).build());
            return ApiResult.builder().result(ExecResult.SUCCESS.code()).message("执行成功").build();
        } else {
            logger.error("[ApiExecService] -> 异步状态码错误，状态码：{}", isAsyn);
            throw new SuperBootException("lhq-superboot-api-0022", isAsyn);
        }
    }

    /**
     * @param params
     * @param format
     * @return
     * @Description: 转化参数为map
     */
    private Map<String, Object> changeParams(String params, String format) {
        try {
            if (FormatType.JSON.code().equals(format)) {
                return FastJsonUtils.stringToCollect(params);
            } else {
                logger.error("[ApiExecService] -> 数据格式无法解析：{}", format);
                throw new SuperBootException("lhq-superboot-api-0023", format);
            }
        } catch (Exception e) {
            logger.error("[ApiExecService] -> 参数转化异常：{}", e.getMessage());
            throw new SuperBootException("lhq-superboot-api-0024", params, format, e.getMessage());
        }
    }

    /**
     * @param paramTypeMap
     * @param paramTypeValue
     * @Description: 检查并且获取参数列表和值列表
     */
    private void checkAndGetValue(LinkedHashMap<String, String> paramTypeMap, Map<String, Object> paramTypeValue,
                                  List<String> paramTypeList, List<Object> paramValueList) {
        Set<String> typeSet = paramTypeMap.keySet();
        Set<String> keySet = paramTypeValue.keySet();
        if (typeSet.size() != keySet.size()) {
            logger.error("[ApiExecService] -> 传参与数据库中参数个数不同");
            throw new SuperBootException("lhq-superboot-api-0025");
        }
        for (String key : typeSet) {
            if (!keySet.contains(key)) {
                logger.error("[ApiExecService] -> 该参数未传值：{}", key);
                throw new SuperBootException("lhq-superboot-api-0026", key);
            }
            paramTypeList.add(paramTypeMap.get(key));
            paramValueList.add(toMapOrList(paramTypeValue.get(key)));
        }
    }

    /**
     * @param jsonObj
     * @return
     * @Description: 将json中的JSONObject和JSONArray转化成map和list（仅一层即可）
     */
    private Object toMapOrList(Object jsonObj) {
        if (jsonObj instanceof JSONObject) {
            return JSON.parseObject(jsonObj.toString(), Map.class);
        }
        if (jsonObj instanceof JSONArray) {
            List<?> jsonList = JSON.parseObject(jsonObj.toString(), List.class);
            return jsonList.stream().map(e -> toMapOrList(e)).collect(Collectors.toList());
        }
        return jsonObj;
    }


}
