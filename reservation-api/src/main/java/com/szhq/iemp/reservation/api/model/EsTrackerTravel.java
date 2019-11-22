package com.szhq.iemp.reservation.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "tracker_travel", type = "travel")
public class EsTrackerTravel {
    //行程id，key
    @Id
    private Long tra_id;
    //设备ID，电信IOT使用
    private String device_id;
    //业务测查询使用
    private String imei;
    //行程日期
    private Date travel_date;
    //行程开始时间
    private long begin_time;
    //结束时间
    private long end_time;
    //行程时长
    private int travel_time;
    //开始位置纬度
    private double begin_lat;
    //开始位置经度
    private double begin_lon;
    //开始位置地址
    private String begin_addr;
    //结束位置纬度
    private double end_lat;
    //结束位置经度
    private double end_lon;
    //结束位置地址
    private String end_addr;
    //行程总里程
    private double travel_mileage;
    //平均时速
    private double avg_speed;
    //最高时速
    private double max_speed;
    //最低时速
    private double min_speed;
    //急加速
    private int rapid_acceleration;
    //急减速
    private int rapid_deceleration;
    //急转弯
    private int rapid_turn;
    //里程起始电量
    private double begin_power;
    //里程结束电量
    private double end_power;
    //行程耗电量
    private double consume_power;
    //违章描述
    private String peccancy;
    //行程评价
    private String travel_assess;
    //附加条款
    private long additional;
}
