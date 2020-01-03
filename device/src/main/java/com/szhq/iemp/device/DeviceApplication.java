package com.szhq.iemp.device;

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
@EntityScan(basePackages = {"com.szhq.iemp.device.api.model", "com.szhq.iemp.common.model"})
@EnableJpaRepositories(basePackages = {"com.szhq.iemp.device.repository", "com.szhq.iemp.common.repository"})
public class DeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceApplication.class, args);
    }

}
