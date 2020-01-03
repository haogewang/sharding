package com.szhq.iemp.common.intercptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.model.AuditLog;
import com.szhq.iemp.common.repository.AuditLogRepository;
import com.szhq.iemp.common.util.DencryptTokenUtil;
import com.szhq.iemp.common.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author wanghao
 * @date 2020/1/2
 */
@Aspect
@Slf4j
@Component
public class AuditLogAspect {

    @Resource
    private AuditLogRepository auditLogRepository;

    @Pointcut("@annotation(com.szhq.iemp.common.resolver.AuditLog)")
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            AuditLog auditLog = new AuditLog();
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            if(attributes == null){
                log.error("attributes is null.");
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            Integer operatorId = DencryptTokenUtil.getOperatorId(request);
            String ip = IpUtil.getIpAddress(request);
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = joinPoint.getTarget().getClass().getName() + "." + method.getName();
            com.szhq.iemp.common.resolver.AuditLog myLog = method.getAnnotation(com.szhq.iemp.common.resolver.AuditLog.class);
            String value = "";
            if (myLog != null) {
                value = myLog.value();
                auditLog.setOperation(value);
            }
            auditLog.setIp(ip);
            auditLog.setOperatorId(operatorId);
            Object[] args = joinPoint.getArgs();
            String params = JSON.toJSONString(args);
            log.info("audit:{},params:{}", JSONObject.toJSONString(auditLog), params);
        } catch (Exception e) {
            log.error("e", e);
        }

    }
}
