package com.szhq.iemp.common.exception;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.ResultUtil;
import com.szhq.iemp.common.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 异常捕获处理类
 */
@ControllerAdvice
public class ExceptionHandle {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private HttpServletRequest request;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e, HttpServletRequest req) {
        req.setAttribute("exception", e);
        req.setAttribute("exmsg", e.getMessage());
        if (e instanceof NbiotException) {
//            return ResultUtil.error(((NbiotException) e).getCode(), e.getMessage());
            Object[] params = ((NbiotException) e).getParams();
            return ResultUtil.error(((NbiotException) e).getCode(), getLocalMessage(((NbiotException) e).getCode(), e.getMessage(), params));
        }
        else if (e instanceof NullPointerException) {
            logger.error("e", e);
//            return ResultUtil.error(CommonConstant.WRONG_CODE, CommonConstant.INNER_ERROR);
            return ResultUtil.error(CommonConstant.WRONG_CODE, getLocalMessage(CommonConstant.WRONG_CODE, e.getMessage()));
        }
        else {
            logger.error("e", e);
            return ResultUtil.error(CommonConstant.WRONG_CODE, e.getMessage());
        }
    }

    private String getLocalMessage(Integer code, String defaultMessage, Object... params){
        String lang = request.getHeader("lang");
        logger.info("lang: " + lang);
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(lang)){
            String[] split = lang.split("_");
            //接收的第一个参数为：语言代码，国家代码
            locale=new Locale(split[0], split[1]);
        }
       String message = messageSource.getMessage(String.valueOf(code), params, defaultMessage, locale);
       logger.info("code:{},message:{}，locale:{}" ,code, message, locale);
       return message;
    }
}
