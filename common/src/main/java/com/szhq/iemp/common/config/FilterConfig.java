package com.szhq.iemp.common.config;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.filter.ParamsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * 从token中解析过滤条件过滤器
 *
 * @author wanghao
 */
@Configuration
public class FilterConfig {
    /**
     * 参数过滤器
     */
    @Bean
    public FilterRegistrationBean<ParamsFilter> parmsFilterRegistration() {

        FilterRegistrationBean<ParamsFilter> registration = new FilterRegistrationBean<ParamsFilter>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new ParamsFilter());
        registration.addUrlPatterns(
                CommonConstant.REGISTER_SEARCH_URL, CommonConstant.REGISTER_SEARCHER_URL, CommonConstant.REGISTER_LIST_URL,
                CommonConstant.ELECMOBILE_SEARCH_URL, CommonConstant.RESERVATION_SEARCH_URL,
                CommonConstant.INSTALLSITE_SEARCH_URL, CommonConstant.DEVICEINVENTORY_SEARCH_URL,
                CommonConstant.DISPACHERLOG_SEARCH_URL, CommonConstant.COUNT_OF_ISP_URL,
                CommonConstant.PUTSTORAGE_STATISTIC_URL, CommonConstant.DISPATCH_STATISTIC_URL,
                CommonConstant.INSTALLSITE_COUNTALL_URL, CommonConstant.DATA_COUNTBYCONTION_URL,
                CommonConstant.DATA_INSTALLSITE_ORDER_URL, CommonConstant.DATA_HISTORY_INSTALLED_STASTIC_URL,
                CommonConstant.BACKOFF_STATISTIC_URL, CommonConstant.BACKOFF_BOXNUMBERS_URL,
                CommonConstant.GET_DISPACHE_BOXNUMBERS_URL, CommonConstant.GET_BACKOFF_BOXNUMBERS_URL,
                CommonConstant.ALARM_LIST_URL, CommonConstant.DATA_INSTALLED_INFO_URL,
                CommonConstant.GET_BOXNUMBERS_BY_PUTSTORAGETIME_URL, CommonConstant.GET_PUTSTORAGE_BOXNUMBERS_URL,
                CommonConstant.OPERATOR_SEARCH,
                CommonConstant.REGISTER_LOG_SEARCH, CommonConstant.NOTRACKER_SEARCH, CommonConstant.REGISTER_COUNT,
                CommonConstant.INSTALLSITE_COUNT_URL, CommonConstant.ELECMOBILE_GETELECINFO_BY_PLATENO,
                CommonConstant.ELECMOBILE_SEARCHALL_URL, CommonConstant.STOREHOUSE_SEARCH_URL,
                CommonConstant.STOREHOUSE_ACTIVE_STATISTIC_URL, CommonConstant.HISTORY_ACTIVE_COUNT,
                CommonConstant.INSTALLSITE_COUNTINSTALLED_URL, CommonConstant.GROUP_SEARCH_URL,
                CommonConstant.INSURANCE_310DEVICES_URL, CommonConstant.INSURANCE_SEARCH_URL);
        registration.setName("paramsFilter");
        registration.setOrder(Integer.MAX_VALUE - 1);
        return registration;
    }
}
