package com.szhq.iemp.reservation.task;

import com.szhq.iemp.reservation.api.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时删除备案预约3天前数据
 */
@Component
public class ReservationTask {
    private static final Logger logger = LoggerFactory.getLogger(ReservationTask.class);

    @Autowired
    private ReservationService reservationService;

    /**
     * 每天凌晨0：00执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void logInfoDelCron() {
        logger.debug("start del reservation three days ago data...");
        Integer count = reservationService.deleteThreeDaysAgoData();
        logger.info("del reservation three days ago data finished,count is:" + count);
    }
}
