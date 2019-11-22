package com.szhq.iemp.reservation.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "history-alarm-data", type = "alarm")
public class EsNbiotDeviceAlarm {

    @Id
    private String id;

    private String imei;

    private Long ts;

    private double lat;

    private double lon;

    private int type;

    private int status;

    private Long createTime;

    private int operator;

    //报警时电池电压
    private double deviceVolt;
    // 紧急报警事件
    private int emergencyAlert;
    //宿主状态
    private int hostMode;
    //运动状态
    private int motionStat;
    //设备故障码
    private int devCode;
    //设备模式
    private int deviceMode;
    //设备类型
    private String devType;
}
