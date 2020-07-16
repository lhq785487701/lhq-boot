package com.lhq.superboot.scheduler.auth;

import java.util.List;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.WhiteListUrl;
import com.lhq.superboot.service.ShiroService;
import com.lhq.superboot.service.WhiteListService;
import com.lhq.superboot.shiro.property.ShiroConfigProperty;
import com.lhq.superboot.util.RedisUtils;

/**
 * @Description: 检查是否权限地址白名单有变动，存在则更新权限
 * @author: lihaoqi
 * @date: 2019年11月11日 下午7:27:49
 * @version: v1.0.0
 */
@Service
public class AuthWhiteUrlTask {

    private static final Logger logger = LoggerFactory.getLogger(AuthWhiteUrlTask.class);

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ShiroConfigProperty shiroProp;

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Autowired
    private ShiroService shiroService;

    @Autowired
    private WhiteListService whiteListService;

    /**
     * @Description: 检查白名单是否发生变化
     */
    public void checkWhiteListUrlIsChange() {
        List<Object> whiteListCache = redisUtils.lGet(shiroProp.getWhiteUrlKey(), 0, -1);
        List<WhiteListUrl> whiteList = whiteListService.selectWhiteListUrl();
        if (!whiteListEqual(whiteListCache, whiteList)) {
            logger.debug("[AuthWhiteUrlTask] -> 白名单列表发生改变，更新权限信息");
            shiroService.updatePermission(shiroFilterFactoryBean);
        }
    }

    /**
     * @param whiteListCache 已经通过id从小到大排序
     * @param whiteList
     * @return
     * @Description: 对比缓存中的白名单与数据库白名单是否一致，一致返回true，否则返回false
     */
    private boolean whiteListEqual(List<Object> whiteListCache, List<WhiteListUrl> whiteList) {
        if (whiteListCache.size() != whiteList.size()) {
            return false;
        }
        if (whiteListCache.isEmpty()) {
            return false;
        }
        int len = whiteListCache.size();
        for (int i = 0; i < len; i++) {
            if (!whiteListCache.get(i).equals(whiteList.get(i).getWhitelistUrl())) {
                return false;
            }
        }
        return true;
    }
}
