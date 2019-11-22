package com.szhq.iemp.common.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Slf4j
public class ParamsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getServletPath();
        log.info("================进入ParamsFilter过滤器=============URL:" + url + "=========");
        ParameterRequestWrapper parmsRequest = new ParameterRequestWrapper((HttpServletRequest) request);
        chain.doFilter(parmsRequest, response);
    }

}
