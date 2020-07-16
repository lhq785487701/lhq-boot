package com.lhq.superboot.api.service;

/**
 * @Description 对外接口api的日志
 * @author: lihaoqi
 * @date: 2019年7月24日 上午10:24:33
 * @version: v1.0.0
 */
public interface ApiLogService {

    /**
     * @param apikey
     * @param result
     * @param params
     * @param message
     * @param method
     * @param isasyn
     * @Description: 写入执行表日志
     */
    public void createLog(String apikey, String result, String params, String message, String method, int isasyn);

}
