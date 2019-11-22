package com.szhq.iemp.reservation.config;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.reservation.intercptor.ValidParameterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

//@Configuration
//public class ParameterValidConfig extends WebMvcConfigurationSupport {
//
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new ValidParameterInterceptor()).addPathPatterns(
//                CommonConstant.NOTRACKER_ADD_REGISTER, CommonConstant.NOTRACKER_BOUND,
//                CommonConstant.NOTRACKER_UNBOUND_DEVICE, CommonConstant.NOTRACKER_UNBOUND, CommonConstant.NOTRACKER_UNBOUND_NO_DELELEC,
//                CommonConstant.REGISTER_URL, CommonConstant.REGISTER_CHANGEIMEI_URL, CommonConstant.REGISTER_DELETE_URL);
//    }
//
//}
