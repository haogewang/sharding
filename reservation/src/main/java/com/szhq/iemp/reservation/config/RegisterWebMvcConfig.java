package com.szhq.iemp.reservation.config;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.reservation.intercptor.ValidParameterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
@Slf4j
@Configuration
public class RegisterWebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ValidParameterInterceptor()).addPathPatterns(
                CommonConstant.NOTRACKER_ADD_REGISTER, CommonConstant.NOTRACKER_ADD_REGISTER_NOIMEI,
                CommonConstant.NOTRACKER_BOUND, CommonConstant.NOTRACKER_BOUND_DEVICES,
                CommonConstant.NOTRACKER_UNBOUND_DEVICE, CommonConstant.NOTRACKER_UNBOUND, CommonConstant.NOTRACKER_UNBOUND_NO_DELELEC,
                CommonConstant.REGISTER_URL, CommonConstant.REGISTER_ADD_URL,
                CommonConstant.REGISTER_CHANGEIMEI_URL, CommonConstant.REGISTER_DELETE_URL);
    }


    /**
     * 配置servlet处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 防止@EnableMvc把默认的静态资源路径覆盖了，手动设置的方式
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        log.debug("");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

}
