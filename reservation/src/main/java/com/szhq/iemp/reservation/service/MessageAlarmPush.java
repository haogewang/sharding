package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.listener.MyWebSocketHandler;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Service
public class MessageAlarmPush{
	private int count = 0;
	@Resource(name = "primaryRedisUtil")
	private RedisUtil redisUtil;
	@Autowired
	private MyWebSocketHandler handler;

//	@PostConstruct
//	public void consumeAlarmData() {
//		log.info("start websocket alarm thread...");
//		Consume consume = new Consume();
//		consume.start();
//	}
	
	
//	public class Consume extends Thread {
//		@Override
//		public void run() {
//			while(true) {
////				log.info("redis list size:" + redisUtil.lsize("alarm-rt-data"));
//				try {
//					String data = (String)redisUtil.brpop("alarm-rt-data");
//					boolean isSuccess = handler.sendMessageToAllUsers(new TextMessage(data));
//					if(count !=0 && count % 100 == 0) {
//						log.info("websocket-alarm-data: " + data + ",result: " + isSuccess);
//						count = 0;
//					}
//					count ++;
//				} catch (Exception e) {
//					log.error("e", e);
//					continue;
//				}
//			}
//		}
//	}
}
