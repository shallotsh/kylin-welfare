package org.kylin.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kylin.constant.Constants;
import org.kylin.util.CommonUtils;
import org.kylin.util.RequestFilterUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(1)
public class AuthFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest) request;
        String servletPath = req.getServletPath();
        log.debug("servletPath:{}", servletPath);

        Object token = req.getSession().getAttribute(Constants.LOGIN_STATUS_KEY);
        if(Objects.equals(token, Constants.LOGIN_SUCCESS) || isIgnoreAuthPath(req.getRequestURI())){
            chain.doFilter(request, response);
            return;
        }

        // 判定是否是谷歌浏览器，临时放行
        if(CommonUtils.isGoogleBrowser(req)){
            log.info("谷歌浏览器临时放行");
            chain.doFilter(request, response);
            return;
        }

        log.warn("非登录访问 client:{},server:{},uri:{}", CommonUtils.getIp(req), req.getServerName(), req.getRequestURI());
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.sendRedirect("/login?origin=" + servletPath);
    }

    @Override
    public void destroy() {

    }


    private boolean isIgnoreAuthPath(String servletPath){
        if(StringUtils.isBlank(servletPath)){
            return false;
        }
        return RequestFilterUtil.isNoAuthRequest(servletPath);
    }
}
