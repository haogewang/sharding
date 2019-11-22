package com.szhq.iemp.reservation.config;

import com.szhq.iemp.reservation.intercptor.WebSocketInterceptor;
import com.szhq.iemp.reservation.listener.MyWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 首先注入一个ServerEndpointExporterBean,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
 * @author wanghao
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		//setAllowedOrigins("*")
		registry.addHandler(new MyWebSocketHandler(), "/websocket/{ID}").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor());
	}
}
