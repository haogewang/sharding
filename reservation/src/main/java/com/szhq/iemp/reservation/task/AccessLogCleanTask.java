package com.szhq.iemp.reservation.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
@Slf4j
public class AccessLogCleanTask {

	@Value("${server.tomcat.accesslog.directory}")
	private String logPath;
	
	//每个星期三中午12点 
	@Scheduled(cron="0 0 12 ? * WED")
	public void cleanTimer(){
		log.info("开始清理tomcat accessLog");
		if (new File(logPath).isDirectory()) {
			// 获取文件夹中的文件集合
			File[] logs = new File(logPath).listFiles();
			// 设置系统这里设置的日期格式,和配置文件里的参数保持一致
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// 遍历集合
			for (int i = 0; i < logs.length; i++) {
				File logger = logs[i];
				log.info("accee log path:" + logger.getAbsolutePath());
				// 获取到第i个日志的名称，截取中间的日期字段,转成long型s
				int start = logger.getName().indexOf(".") + 1;
				int end = logger.getName().lastIndexOf(".");
				// 获取到的日志名称中的时间（2019-05-29）
				String dateStr = logger.getName().substring(start, end);
				// 将字符串型的（2019-05-29）转换成long型
				long lonInt = 0;
				try {
					lonInt = dateFormat.parse(dateStr).getTime();
					// 系统时间减去日志名字中获取的时间差大于配置文件中设置的时间删除
					if ((System.currentTimeMillis() - lonInt) / (1000 * 60 * 60 * 24) >= 15) {
						logger.delete();
						System.out.println(logger.getName());
					}
				} catch (ParseException e) {
					log.error("删除accessLog出错",e);
				}
			}
		}
	}
}
