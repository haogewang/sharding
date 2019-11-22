package com.szhq.iemp.common.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tomcat配置
 *
 * @author wanghao
 * @date 2019/9/2
 */
@Configuration
public class TomCatConfig {

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(
                connector -> {
                    Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                    protocol.setDisableUploadTimeout(false);
                    //最大并发数，默认设置200
                    protocol.setMaxThreads(500);
                    //Tomcat 初始化时创建的线程数
                    protocol.setMinSpareThreads(25);
                    //指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理，默认设置100
                    protocol.setAcceptCount(200);
                    protocol.setConnectionTimeout(30000);
                    //maxKeepAliveRequests="1"表示每个连接只响应一次就关闭，这样就不会等待timeout了,
                    // 避免tomcat产生大量的TIME_WAIT连接，从而从一定程度上避免tomcat假死。
                    protocol.setMaxKeepAliveRequests(1);
                });
        return factory;
    }
}
