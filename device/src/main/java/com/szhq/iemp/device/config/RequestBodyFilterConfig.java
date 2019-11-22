package com.szhq.iemp.device.config;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.device.filter.RequsetBodyChannelFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
public class RequestBodyFilterConfig {
	/**
	 * body过滤器
	 */
	@Bean
	public FilterRegistrationBean<RequsetBodyChannelFilter> requestBodyFilterRegistration() {
		StringBuffer excludedUriStr = new StringBuffer();
        excludedUriStr.append("/actuator/*");
        excludedUriStr.append(",");
        excludedUriStr.append("/actuator");
        excludedUriStr.append(",");
        excludedUriStr.append("/favicon.ico");
        excludedUriStr.append(",");
        excludedUriStr.append("/js/*");
		
		FilterRegistrationBean<RequsetBodyChannelFilter> registration = new FilterRegistrationBean<RequsetBodyChannelFilter>();
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.setFilter(new RequsetBodyChannelFilter());
		registration.addUrlPatterns(
				CommonConstant.DISPACHE_BY_IMEIS_URL, CommonConstant.DISPACHE_BY_BOXNUMBER_URL,
				CommonConstant.BACK_BY_BOXNUMBER_URL, CommonConstant.BACK_BY_IMEIS_URL,
				CommonConstant.PUT_INTO_STORAGE_URL,
				CommonConstant.VALID_PUTSTORAGE_BY_BOXNUMBERS_URL, CommonConstant.VALID_IMEI_INFO_URL);
		registration.addInitParameter("excludedUri", excludedUriStr.toString());
		registration.setName("requsetBodyFilter");
		registration.setOrder(1);
		return registration;
	}
}
