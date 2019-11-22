package com.szhq.iemp.common.intercptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ContentSecurityConstant;
import com.szhq.iemp.common.resolver.ContentSecurity;
import com.szhq.iemp.common.util.AESUtils;
import com.szhq.iemp.common.util.DES3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * 安全认证拦截器
 */
public class ContentSecurityInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ContentSecurityInterceptor.class);

    /**
     * 请求之前处理加密内容
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //默认可以通过
        boolean isPass = true;
        //获取请求映射方法对象
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //获取访问方法实例对象
        Method method = handlerMethod.getMethod();
        //检查是否存在内容安全验证注解
        ContentSecurity security = method.getAnnotation(ContentSecurity.class);
        //存在注解做出不同方式认证处理
        if (security != null) {
            switch (security.away()) {
                //DES方式内容加密处理
                case DES:
                    isPass = checkDES(request, response);
                    break;
                case AES:
                    isPass = checkAES(request, response);
                default:
                    break;
            }
        }
        return isPass;
    }

    private boolean checkAES(HttpServletRequest request, HttpServletResponse response) {
        try {
            //获取aesString加密内容
            String aes = request.getParameter(ContentSecurityConstant.AES_PARAMETER_NAME);
            logger.info("请求加密参数内容：{}", aes);
            //加密串不存在
            if (aes == null || aes.length() == 0) {
                JSONObject json = new JSONObject();
                json.put("msg", "The AES Content Security Away Request , Parameter Required is " + ContentSecurityConstant.AES_PARAMETER_NAME);
                response.getWriter().print(JSON.toJSONString(json));
                return false;
            }
            //存在加密串,解密AES参数列表并重新添加到request内
            aes = AESUtils.AESDecode(aes);
            if (!StringUtils.isEmpty(aes)) {
                JSONObject params = JSON.parseObject(aes);
                logger.info("解密请求后获得参数列表  >>> {}", aes);
                Iterator<?> it = params.keySet().iterator();
                while (it.hasNext()) {
                    //获取请求参数名称
                    String parameterName = it.next().toString();
                    //参数名称不为空时将值设置到request对象内
                    if (!StringUtils.isEmpty(parameterName)) {
                        request.setAttribute(ContentSecurityConstant.ATTRIBUTE_PREFFIX + parameterName, params.get(parameterName));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            JSONObject json = new JSONObject();
            json.put("msg", "The AES Content Security Error." + ContentSecurityConstant.AES_PARAMETER_NAME);
            try {
                response.getWriter().print(JSON.toJSONString(json));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * 检查DES方式内容
     *
     * @param request
     * @param response
     * @return
     */
    boolean checkDES(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取desString加密内容
        String des = request.getParameter(ContentSecurityConstant.DES_PARAMETER_NAME);
        logger.info("请求加密参数内容：{}", des);
        //加密串不存在
        if (des == null || des.length() == 0) {
            JSONObject json = new JSONObject();
            json.put("msg", "The DES Content Security Away Request , Parameter Required is " + ContentSecurityConstant.DES_PARAMETER_NAME);
            response.getWriter().print(JSON.toJSONString(json));
            return false;
        }
        //存在加密串,解密DES参数列表并重新添加到request内
        try {
            des = DES3Util.decrypt(des, DES3Util.DESKEY, "UTF-8");
            if (!StringUtils.isEmpty(des)) {
                JSONObject params = JSON.parseObject(des);
                logger.info("解密请求后获得参数列表  >>> {}", des);
                Iterator<?> it = params.keySet().iterator();
                while (it.hasNext()) {
                    //获取请求参数名称
                    String parameterName = it.next().toString();
                    //参数名称不为空时将值设置到request对象内
                    if (!StringUtils.isEmpty(parameterName)) {
                        request.setAttribute(ContentSecurityConstant.ATTRIBUTE_PREFFIX + parameterName, params.get(parameterName));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            JSONObject json = new JSONObject();
            json.put("msg", "The DES Content Security Error." + ContentSecurityConstant.DES_PARAMETER_NAME);
            response.getWriter().print(JSON.toJSONString(json));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
