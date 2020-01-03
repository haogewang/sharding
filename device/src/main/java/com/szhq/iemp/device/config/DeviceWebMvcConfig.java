package com.szhq.iemp.device.config;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.device.intercptor.ParameterValidInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class DeviceWebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ParameterValidInterceptor()).addPathPatterns(
                CommonConstant.PUT_INTO_STORAGE_URL, CommonConstant.PUT_INTO_STORAGE_BY_DELIVERSN_URL,
                CommonConstant.DISPACHE_BY_IMEIS_URL, CommonConstant.DISPACHE_BY_BOXNUMBER_URL,
                CommonConstant.BACK_BY_IMEIS_URL, CommonConstant.BACK_BY_BOXNUMBER_URL,
                CommonConstant.VALID_IMEI_INFO_URL, CommonConstant.VALID_PUTSTORAGE_BY_BOXNUMBERS_URL,
                CommonConstant.DISPATCH_TO_DEVICE_GROUP_URL, CommonConstant.DISPATCH_TO_ELEC_GROUP_URL, CommonConstant.REMOVE_GROUP,
                CommonConstant.DEVICE_ACTIVE_URL, CommonConstant.DEVICE_UN_ACTIVE_URL);
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
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
}
