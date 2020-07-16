package com.lhq.superboot.service.impl;

import com.lhq.superboot.domain.Resource;
import com.lhq.superboot.domain.WhiteListUrl;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.ResourceMapper;
import com.lhq.superboot.service.ShiroService;
import com.lhq.superboot.service.WhiteListService;
import com.lhq.superboot.shiro.property.ShiroConfigProperty;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: shiro权限
 * @author: lihaoqi
 * @date: 2019年4月18日
 * @version: 1.0.0
 */

@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {

    private static final Logger logger = LoggerFactory.getLogger(ShiroServiceImpl.class);

    @Autowired
    private ResourceMapper resMapper;

    @Autowired
    private ShiroConfigProperty shiroProp;

    @Autowired
    private WhiteListService whiteListService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String, String> loadFilterChainDefinitions() {
        logger.info("[Shiro] -> loadFilterChainDefinitions()");
        List<Resource> resList = resMapper.selectByExample(null);
        // 权限控制map.从数据库获取
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>(512);
        if (!resList.isEmpty()) {
            String resCode;
            String resUrl;
            for (Resource resource : resList) {
                resCode = resource.getResCode();
                resUrl = resource.getResUrl();
                if (StringUtils.isEmpty(resCode) || StringUtils.isEmpty(resUrl)) {
                    logger.warn("id为：" + resource.getResId() + "的资源缺失资源路径或资源编码");
                    continue;
                }
                filterChainDefinitionMap.put(resource.getResUrl(), "perms[" + resource.getResCode() + "]");
            }
        }
        // 白名单地址排除在外,并存入缓存
        List<WhiteListUrl> whiteList = whiteListService.selectWhiteListUrl();
        if (!whiteList.isEmpty()) {
            for (WhiteListUrl whiteUrl : whiteList) {
                filterChainDefinitionMap.put(whiteUrl.getWhitelistUrl(), "anon");
            }
            // 将白名单存入缓存
            String whiteKey = shiroProp.getWhiteUrlKey();
            if (redisUtils.hasKey(whiteKey)) {
                redisUtils.del(whiteKey);
            }
            redisUtils.lrSetList(whiteKey, whiteList.stream().map(WhiteListUrl::getWhitelistUrl).collect(Collectors.toList()));
        }
        // swagger2
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/configuration/security", "anon");
        filterChainDefinitionMap.put("/configuration/ui", "anon");

        filterChainDefinitionMap.put(shiroProp.getAuthUrl(), "error");
        filterChainDefinitionMap.put(shiroProp.getAuthUrl(), "perms");
        filterChainDefinitionMap.put(shiroProp.getAuthUrl(), "authc");
        return filterChainDefinitionMap;
    }

    @Override
    public void updatePermission(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        synchronized (this) {
            AbstractShiroFilter shiroFilter;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            } catch (Exception e) {
                throw new SuperBootException("get ShiroFilter from shiroFilterFactoryBean error!");
            }
            if (shiroFilter == null) {
                throw new SuperBootException("shiroFilter is null");
            }

            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                    .getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            // 清空老的权限控制
            manager.getFilterChains().clear();

            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            logger.info("[Shiro] -> updatePermission清除缓存成功,重新加载缓存");
            shiroFilterFactoryBean.setFilterChainDefinitionMap(loadFilterChainDefinitions());
            // 重新构建生成
            Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                manager.createChain(url, chainDefinition);
            }

        }
    }
}
