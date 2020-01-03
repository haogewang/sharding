package com.szhq.iemp.device.filter;

import com.iemp.zuul.config.BodyReaderHttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * POST请求body复制
 * @author wanghao
 */
public class RequsetBodyChannelFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequsetBodyChannelFilter.class);

    private String[] excludedUris;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init RequestBodyChannelFilter...");
        excludedUris = filterConfig.getInitParameter("excludedUri").split(",");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String uri = httpServletRequest.getServletPath();
        if (isExcludedUri(uri)) {
            return;
        }
        if (!HttpMethod.POST.name().equals(httpServletRequest.getMethod())) {
            logger.info("method:" + httpServletRequest.getMethod() + ", URL:" + uri);
            chain.doFilter(request, response);
            return;
        }
        logger.info("================进入RequsetBodyChannelFilter过滤器=========URL:" + uri + "=============");
        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        //logger.info("Method:" + httpServletRequest.getMethod() + ",URL:" + httpServletRequest.getRequestURL());
        ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
        chain.doFilter(requestWrapper, response);
    }


    private boolean isExcludedUri(String uri) {
        if (excludedUris == null || excludedUris.length <= 0) {
            return false;
        }
        for (String ex : excludedUris) {
            uri = uri.trim();
            ex = ex.trim();
            if (uri.toLowerCase().matches(ex.toLowerCase().replace("*", ".*")))
                return true;
        }
        return false;
    }

}
