package com.szhq.iemp.common.util;

import com.szhq.iemp.common.resolver.NonWebRequestAttributes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public final class SecurityUtils {

    @Autowired
    private JwtTokenUtil jwtToken;

    public SecurityUtils() {

    }

    private RequestAttributes getRequestAttributesSafely() {
        RequestAttributes requestAttributes = null;
        try {
            requestAttributes = RequestContextHolder.getRequestAttributes();
        } catch (IllegalStateException e) {
            requestAttributes = new NonWebRequestAttributes();
        }
        return requestAttributes;
    }


    /**
     * 获取当前登录的用户
     * @return 返回用户id
     */
    public String getCurrentUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributesSafely();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        if (request.getRequestURI() != null && request.getRequestURI().endsWith("/reservation/save")) {
            return null;
        }
        if (StringUtils.isEmpty(request.getHeader("token"))) {
            return null;
        }
        return jwtToken.verifyToken(request.getHeader("token"));
    }

}
