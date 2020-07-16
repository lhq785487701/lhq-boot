package com.lhq.superboot.api.service;

import com.lhq.superboot.api.domain.ApiResult;
import com.lhq.superboot.domain.SysApiExecute;

/**
 * @Description 对外接口api的执行接口
 * @author: lihaoqi
 * @date: 2019年7月24日 上午10:24:33
 * @version: v1.0.0
 */
public interface ApiExecService {

    /**
     * @return
     * @Description:通过exceCode获取执行方法
     */
    public SysApiExecute getApiExceByCode(String exceCode);

    /**
     * @param apikey
     * @param className
     * @param methodName
     * @param paramType
     * @param params
     * @param format
     * @param isAsyn
     * @param rtnUrl
     * @return
     * @Description:执行对应的方法
     */
    public ApiResult exce(String apikey, String className, String methodName, String paramType, String params, String format, int isAsyn, String rtnUrl);

}
