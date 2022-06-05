package org.kylin.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.kylin.util.CommonUtils;
import org.kylin.util.RequestFilterUtil;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Slf4j
@Component
@WebFilter(urlPatterns = "/*", filterName = "logTraceFilter")
@Order(0)
public class LogTraceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("config={}", filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req=(HttpServletRequest) request;
        String servletPath = req.getRequestURI();
        if(RequestFilterUtil.isStaticResourceRequest(servletPath)){
            chain.doFilter(request, response);
            return;
        }

        StopWatch watch = new StopWatch();
        watch.start();

        if(request instanceof HttpServletRequest) {
            String requestId = System.currentTimeMillis() / 1000 + RandomStringUtils.randomNumeric(4);
            MDC.put("requestId", requestId);
            recordDetailLog(request);
        }

        chain.doFilter(request, response);

        watch.stop();
        log.info("请求耗时:{}", watch.getTotalTimeMillis());
    }

    private void recordDetailLog(ServletRequest request) {
        log.info("request ip={},serverName={}, uri={}, authType={}", CommonUtils.getIp((HttpServletRequest)request),
                request.getServerName(), ((HttpServletRequest) request).getRequestURI(), ((HttpServletRequest) request).getAuthType());
    }



    @Override
    public void destroy() {
    }
}
