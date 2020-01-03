package com.szhq.iemp.reservation;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.szhq.iemp.*"})
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@EnableDubboConfiguration
@EntityScan(basePackages = {"com.szhq.iemp.common.model", "com.szhq.iemp.reservation.api.model"})
@EnableJpaRepositories(basePackages = {"com.szhq.iemp.common.repository", "com.szhq.iemp.reservation.repository"})
public class ReservationApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(ReservationApplication.class, args);
    }

}
