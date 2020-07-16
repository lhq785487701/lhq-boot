package com.lhq.superboot.controller.api;

import com.lhq.superboot.api.domain.ApiEntity;
import com.lhq.superboot.api.domain.ApiResult;
import com.lhq.superboot.api.enums.ExecResult;
import com.lhq.superboot.api.service.ApiExecService;
import com.lhq.superboot.api.service.ApiLogService;
import com.lhq.superboot.api.utils.ApiCheckUtils;
import com.lhq.superboot.domain.SysApiExecute;
import com.lhq.superboot.exception.SuperBootException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 对外接口api总接口
 * @author: lihaoqi
 * @date: 2019/12/23 9:55
 * @version: v1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@Api(value = "对外接口controller", tags = {"对外接口操作接口"})
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private ApiCheckUtils apiCheckUtils;

    @Autowired
    private ApiExecService apiExecService;

    @Autowired
    private ApiLogService apiLogService;

    @PostMapping("/api")
    public Object api(HttpServletRequest request) {
        String params = null, method = null, apikey = null, methodName, classPath, rtnUrl, format, paramType, timestamp, execcode, sign, version;
        int isAsyn = 0;
        try {
            // 先取出必要参数(方便记录日志)
            params = request.getParameter("data");
            rtnUrl = request.getParameter("asynRtnUrl");
            format = request.getParameter("format");
            apikey = request.getParameter("apikey");
            isAsyn = Integer.parseInt(request.getParameter("isAsyn"));
            timestamp = request.getParameter("timestamp");
            execcode = request.getParameter("execcode");
            sign = request.getParameter("sign");
            version = request.getParameter("version");
            // 封装为参数，方便传送
            ApiEntity apiEntity = new ApiEntity().toBuilder()
                    .apikey(apikey).format(format).data(params)
                    .timestamp(timestamp).execcode(execcode)
                    .sign(sign).version(version).isAsyn(isAsyn)
                    .asynRtnUrl(rtnUrl).build();
            logger.info("[ApiController] -> 传入参数：{}", apiEntity);
            // 校验参数
            apiCheckUtils.checkParams(apiEntity);

            SysApiExecute sysApiExecute = apiExecService.getApiExceByCode(execcode);
            if (sysApiExecute == null) {
                throw new SuperBootException("lhq-superboot-api-0016");
            }
            logger.info("[ApiController] -> execcode:{}, execname:{}", execcode, sysApiExecute.getExecName());

            methodName = sysApiExecute.getExecMethod();
            classPath = sysApiExecute.getExecClass();
            method = classPath + "." + methodName;
            paramType = sysApiExecute.getExecParam();
            ApiResult apiResult = apiExecService.exce(apikey, classPath, methodName, paramType, params, format, isAsyn, rtnUrl);
            // 同步时记录日志、异步在线程中记录日志
            if (isAsyn == 0) {
                apiLogService.createLog(apikey, apiResult.getResult(), params, apiResult.getMessage(), method, isAsyn);
            }
            return ApiResult.builder().result(apiResult.getResult()).message(apiResult.getMessage()).data(apiResult.getData()).build();
        } catch (SuperBootException e) {
            logger.error("[ApiController] -> api接口报错，错误信息：{}", e.getMessage());
            apiLogService.createLog(apikey, ExecResult.ERROR.code(), params, e.getMessage(), method, isAsyn);
            return ApiResult.builder().result(ExecResult.ERROR.code()).message(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("[ApiController] -> api接口未知报错，错误信息：{}", e);
            apiLogService.createLog(apikey, ExecResult.ERROR.code(), params, e.getMessage(), method, isAsyn);
            return ApiResult.builder().result(ExecResult.ERROR.code()).message(e.getMessage()).build();
        }
    }

    @PostMapping("/api/rtn")
    public Object apiRtn(HttpServletRequest request) throws Exception {
        logger.debug("回调测试");
        String json = new String(readInputStream(request.getInputStream()), StandardCharsets.UTF_8);
        logger.debug("回调获取参数：{}", json);
        return "调用成功";
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

}
