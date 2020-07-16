package com.lhq.superboot.service;

import java.util.List;

import com.lhq.superboot.domain.WhiteListUrl;

/**
 * @Description: 白名单
 * @author: lihaoqi
 * @date: 2019年8月12日 下午11:45:48
 * @version: v1.0.0
 */
public interface WhiteListService {

    /**
     * @return
     * @Description: 获取系统白名单
     */
    public List<WhiteListUrl> selectWhiteListUrl();

}
