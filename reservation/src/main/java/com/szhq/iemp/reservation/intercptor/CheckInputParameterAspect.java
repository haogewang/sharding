package com.szhq.iemp.reservation.intercptor;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.IllegalStrFilterUtil;
import com.szhq.iemp.reservation.api.model.Tregistration;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.vo.NotrackerRegister;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 对传入的参数进行校验
 * @author wanghao
 * @date 2019/11/14
 */
@Component
@Aspect
@Slf4j
public class CheckInputParameterAspect {

    /**
     * 定义切入点:拦截controller层指定方法
     */
    @Pointcut("execution(public * com.szhq.iemp.reservation.controller.NoTrackerElecController.addRegister(..))")
    public void addRegister() {
    }
    @Pointcut("execution(public * com.szhq.iemp.reservation.controller.RegisterationController.createRegistration(..))")
    public void createRegistration() {
    }

    /**
     * 定义环绕通知
     */
    @Around("addRegister() || createRegistration()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            log.error("attributes is null.");
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName(); //获取方法名称
        Object[] args = joinPoint.getArgs();//参数
        Object[] arguments  = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            //ServletRequest不能序列化，从入参里排除
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
                    || args[i] instanceof MultipartFile || args[i] instanceof BindingResult) {
                continue;
            }
            arguments[i] = args[i];
        }
        log.info("args:" + JSONObject.toJSONString(arguments));
        //获取被切参数名称及参数值
        Map<String, Object> nameAndArgsMap = getFieldsName(this.getClass(), clazzName, methodName, args);
        log.info("当前调用接口-[" + request.getRequestURI() + "]");
        if(request.getRequestURI().endsWith(CommonConstant.NOTRACKER_ADD_REGISTER)){
            NotrackerRegister data = (NotrackerRegister)nameAndArgsMap.get("data");
            log.info("user:" + data.getUser());
            valid(data.getUser());
        }
        if(request.getRequestURI().endsWith(CommonConstant.REGISTER_URL)){
            Tregistration data = (Tregistration)nameAndArgsMap.get("data");
            valid(data.getUser());
        }
        Object result = joinPoint.proceed();
        return result;
    }

    private void valid(Tuser user) {
        if (user != null) {
            valid(user.getIdNumber());
            valid(user.getPhone());
            valid(user.getContactPhone());
            valid(user.getHome());
        }
    }

    private void valid(String value) {
        if (StringUtils.isNotEmpty(value)) {
            Boolean valid = IllegalStrFilterUtil.isIllegalStr(value);
            log.info("valid:" + valid);
            if (valid) {
                log.error("contain illegal char." + value);
                throw new NbiotException(600, "");
            }
        }
    }

    private Map<String, Object> getFieldsName(Class<? extends CheckInputParameterAspect> aClass, String clazzName, String methodName, Object[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(aClass);
            pool.insertClassPath(classPath);
            CtClass cc = pool.get(clazzName);
            CtMethod cm = cc.getDeclaredMethod(methodName);
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            //如果是静态方法，则第一个就是参数,如果不是静态方法，则第一个是"this"，然后才是方法的参数
            int pos = Modifier.isStatic(cm.getModifiers()) ? 1 : 2;
            for (int i = 0; i < cm.getParameterTypes().length; i++) {
                //paramNames即参数名
                map.put(attr.variableName(i + pos), args[i]);
            }
        } catch (NotFoundException e) {
            log.error("e", e);
        }
        return map;
    }

}
