package com.szhq.iemp.common.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.szhq.iemp.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Activate(group = Constants.PROVIDER, before = {"exception"}, value = {"customException"})
public class CustomExceptionFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CustomExceptionFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        setRequest(invocation);
        Result result = invoker.invoke(invocation);
        if (result.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = result.getException();
                // 如果是checked异常，直接抛出
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return result;
                }
                // 在方法签名上有声明，直接抛出
                try {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Class<?>[] exceptionClassses = method.getExceptionTypes();
                    for (Class<?> exceptionClass : exceptionClassses) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return result;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    return result;
                }
                // 是JDK自带的异常，直接抛出
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    return result;
                }
                // 是Dubbo本身的异常，直接抛出
                if (exception instanceof RpcException) {
                    return result;
                }
                //其他exception ，减少问题，直接将exception序列化成RuntimeException，同时放入指定的异常类型值attachment中
                // 否则，包装成RuntimeException抛给客户端
                RpcResult rpcResult = new RpcResult(new RuntimeException(exception.getMessage()));
                rpcResult.setAttachment("customException", exception.getClass().getName());//已经包装过后续ExceptionFilter无需处理
                result = rpcResult;
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                return result;
            }
        }
        return result;
    }

    /**
     * 设置接口调用参数
     */
    protected void setRequest(Invocation invocation) {
        try {
            Object[] arguments = invocation.getArguments();
            if (arguments != null && arguments.length > 0) {
                for (Object o : arguments) {
                    if (o instanceof HttpServletRequest) {
                        SecurityUtils securityUtils = new SecurityUtils();
//                        securityUtils.setRequest((HttpServletRequest) o);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("e", e);
        }
    }
}
