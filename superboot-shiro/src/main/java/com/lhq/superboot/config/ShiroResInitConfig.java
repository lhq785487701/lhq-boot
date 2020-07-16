package com.lhq.superboot.config;

import com.lhq.superboot.domain.Resource;
import com.lhq.superboot.domain.ResourceExample;
import com.lhq.superboot.domain.WhiteListUrl;
import com.lhq.superboot.enums.LoginSource;
import com.lhq.superboot.mapper.ResourceMapper;
import com.lhq.superboot.service.ShiroService;
import com.lhq.superboot.service.WhiteListService;
import com.lhq.superboot.util.SpringContextUtil;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @Description: 初始化将接口权限写入
 * @author: lihaoqi
 * @date: 2019年4月26日
 * @version: 1.0.0
 */

@Component
@Order(2)
public class ShiroResInitConfig implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(ShiroResInitConfig.class);

    /**
     * 包前缀
     **/
    private static final String PACKAGE_PREFIX = "com.lhq.superboot.controller";

    /**
     * 微信接口名、后台接口名、PC接口名
     **/
    private static final String WECHAT_URL_NAME = "/xcx/";
    private static final String PC_URL_NAME = "/pc/";
    private static final String HT_URL_NAME = "/ht/";

    /**
     * 创建人
     **/
    private static final String CREATE_NAME = "system";

    @Autowired
    private ResourceMapper resMapper;

    @Autowired
    private WhiteListService whiteListService;

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Autowired
    private ShiroService shiroService;

    @Override
    public void run(String... args) {
        try {
            setNotUrlRes(getAllUrl());
            log.info("[ShiroResInitConfig] -> 资源权限初始化完成");
        } catch (Exception e) {
            log.error("[ShiroResInitConfig] -> 资源权限初始化异常", e);
        }
    }

    /**
     * @Description: 将所有没有设置进去数据库的资源权限设置进数据库
     */
    @Transactional
    public void setNotUrlRes(List<Map<String, String>> allUrl) {
        ResourceExample resExample = new ResourceExample();
        resExample.createCriteria().andResUrlIn(allUrl.stream().map(urlMap -> urlMap.get("url")).collect(Collectors.toList()));

        List<Resource> resList = resMapper.selectByExample(resExample);

        allUrl = allUrl.stream().filter(allMap -> (resList.stream()
                .filter(res -> res.getResUrl().equals(allMap.get("url"))).findAny().orElse(null)) == null)
                .collect(Collectors.toList());
        if (allUrl.isEmpty()) {
            log.debug("[ShiroResInitConfig] -> 全部接口已经载入");
            return;
        }
        log.debug("[ShiroResInitConfig] -> 尚未插入资源表的接口 : {}", allUrl);
        for (Map<String, String> resMap : allUrl) {
            String url = resMap.get("url");
            String method = resMap.get("method");
            String urlName = url.replace("/", "");
            String channel = "";
            if (url.contains(WECHAT_URL_NAME)) {
                channel = LoginSource.XCX.name();
            } else if (url.contains(PC_URL_NAME)) {
                channel = LoginSource.PC.name();
            } else if (url.contains(HT_URL_NAME)) {
                channel = LoginSource.HT.name();
            }

            Resource res = new Resource().toBuilder().remark(method).resName(urlName).resCode(urlName).resUrl(url)
                    .createUser(CREATE_NAME).modifyUser(CREATE_NAME).resChannel(channel).build();
            resMapper.insertSelective(res);
            log.debug("[ShiroResInitConfig] -> 插入资源 : {}", url);
        }
        shiroService.updatePermission(shiroFilterFactoryBean);
    }

    /**
     * @Description: 查询出所有controller接口
     */
    private List<Map<String, String>> getAllUrl() {
        // 如果不加requestMappingHandlerMapping可能与swagger的冲突
        RequestMappingHandlerMapping mapping = SpringContextUtil.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<Map<String, String>> list = new ArrayList<>();
        for (Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Map<String, String> mapUrl = new HashMap<>(512);
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            // 不属于自己定义的包不写入
            String className = method.getMethod().getDeclaringClass().getName();
            if (className == null || !className.contains(PACKAGE_PREFIX)) {
                continue;
            }
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                mapUrl.put("url", url);
            }
            mapUrl.put("className", method.getMethod().getDeclaringClass().getName());
            mapUrl.put("method", method.getMethod().getName());
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                mapUrl.put("type", requestMethod.toString());
            }

            list.add(mapUrl);
        }
        log.trace("[ShiroResInitConfig] -> 获取接口信息 : {}", list);
        List<String> whiteList = getWhiteList();
        log.trace("[ShiroResInitConfig] -> 获取白名单地址 : {}", whiteList);
        return list.stream().filter(authUrl -> !filterUnAuthUrl(authUrl.get("url"), whiteList)).collect(Collectors.toList());
    }

    /**
     * @param url
     * @param whiteList 白名单地址列表
     * @return
     * @Description: 如果是白名单，返回true
     */
    private boolean filterUnAuthUrl(String url, List<String> whiteList) {
        for (String unAuthUrl : whiteList) {
            if (url.equals(unAuthUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return 返回白名单url列表
     * @Description: 获取白名单地址
     */
    private List<String> getWhiteList() {
        return whiteListService.selectWhiteListUrl().stream().map(WhiteListUrl::getWhitelistUrl).collect(Collectors.toList());
    }
}
