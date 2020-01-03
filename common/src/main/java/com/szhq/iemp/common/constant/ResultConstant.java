package com.szhq.iemp.common.constant;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.Locale;

@Slf4j
public enum ResultConstant {

    FAILED(0, "失败"),
    SUCCESS(1, "成功");

    public int code;
    public String message;

    private MessageSource messageSource;
    private HttpServletRequest request;

    ResultConstant(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultConstant setMessageSource(MessageSource messageSource, HttpServletRequest request) {
        this.messageSource = messageSource;
        this.request = request;
        return this;
    }

    //通过静态内部类的方式注入bean，并赋值到枚举中
    @Component
    public static class ReportTypeServiceInjector {
        @Autowired
        private MessageSource messageSource;
        @Autowired
        private HttpServletRequest request;
        @PostConstruct
        public void postConstruct() {
            for (ResultConstant rt : EnumSet.allOf(ResultConstant.class))
                rt.setMessageSource(messageSource, request);
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
//        return message;
        String lang = request.getHeader("lang");
        Locale locale = Locale.getDefault();
        if (StringUtils.isNotEmpty(lang)){
            String[] split = lang.split("_");
            if(split.length < 2){
                locale=new Locale("ZH", "CN");
            }else{
                //接收的第一个参数为：语言代码，国家代码
                locale=new Locale(split[0], split[1]);
            }
        }
//        log.info("lang:{} local:{}",lang, locale);
        return messageSource.getMessage(String.valueOf(code),null, message, locale);
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
