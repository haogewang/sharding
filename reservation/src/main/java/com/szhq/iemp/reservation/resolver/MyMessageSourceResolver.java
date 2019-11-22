package com.szhq.iemp.reservation.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 国际化
 * @author wanghao
 * @date 2019/11/15
 */
public class MyMessageSourceResolver implements MessageSourceResolvable {

//    @Override
//    public Locale resolveLocale(HttpServletRequest request) {
//        //获取自定义请求头信息，lang的参数值
//        String lang=request.getHeader("lang");
//        //获取系统的默认区域信息
//        Locale locale = Locale.getDefault();
//        if (!StringUtils.isEmpty(lang)){
//            String[] split=lang.split("_");
//            //接收的第一个参数为：语言代码，国家代码
//            locale=new Locale(split[0], split[1]);
//        }
//        return locale;
//    }
//
//    @Override
//    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
//
//    }

    private Integer code;

    private String message;

    @Override
    public String[] getCodes() {
        return new String[code];
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public String getDefaultMessage() {
        return message;
    }

    public MyMessageSourceResolver(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
