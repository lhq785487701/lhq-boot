package com.lhq.superboot.shiro.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lhq.superboot.enums.LoginMsg;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019年11月19日 下午6:57:05
 * @version: v1.0.0
 */
public class LocalFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(FormAuthenticationFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        logger.error("[shiro] -> 进入登录失败过滤器");
        Subject subject = getSubject(request, response);
        // 未登录
        if (subject.getPrincipal() == null) {
            super.saveRequest(request);
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, LoginMsg.UNLOGIN.getCode());
        } else {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_FORBIDDEN, LoginMsg.UNAUTH.getCode());
        }
        return false;
    }
}