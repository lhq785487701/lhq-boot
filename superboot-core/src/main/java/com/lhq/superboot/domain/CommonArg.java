package com.lhq.superboot.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 公共参数封装
 * @author: lihaoqi
 * @date: 2019年8月8日 下午4:59:17
 * @version: v1.0.0
 */
public class CommonArg {

    /**
     * 原始参数
     **/
    private Object[] args = null;

    /**
     * 参数列表
     **/
    private Map<String, Object> params = new HashMap<String, Object>();

    /**
     * 当前请求
     **/
    private HttpServletRequest request = null;

    private HttpServletResponse response = null;

    /**
     * @param key
     * @param val
     * @Description: 添加参数
     */
    public void addParam(String key, Object val) {
        this.params.put(key, val);
    }

    /**
     * @param params
     * @Description: 添加参数
     */
    public void addParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * @param key
     * @return
     * @Description: 获取参数
     */
    public Object getParam(String key) {
        return this.params.get(key);
    }

    /**
     * @return
     * @Description: 获取参数entry
     */
    public Set<Entry<String, Object>> getParamEntrys() {
        return this.params.entrySet();
    }

    /**
     * @return
     * @Description: 获取当前数据源参数 包含用户全局参数
     */
    public Map<String, Object> getParams() {
        Map<String, Object> m = new HashMap<String, Object>(40);
        if (this.params != null) {
            m.putAll(this.params);
        }
        return m;
    }

    /**
     * @return
     * @Description: 获取参数
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args
     * @Description: 设置参数
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
