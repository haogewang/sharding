package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import com.szhq.iemp.reservation.api.vo.query.AlarmQuery;

import java.util.Map;


public interface EsNbiotDeviceAlarmService {

    /**
     * 告警列表
     */
    MyPage<EsNbiotDeviceAlarm> alarmList(Integer page, Integer size, String sorts, String orders, AlarmQuery alarmQuery);

    /**
     * 告警统计
     */
    Map<Long, Long> alarmStastic(AlarmQuery query);

    /**
     * 电动车告警排名
     */
    Map<String, Long> elecAlarmSort(AlarmQuery alarmQuery);

    /**
     * 告警数量统计
     */
    Long elecAlarmCount(AlarmQuery alarmQuery);
    /**
     *根据imei删除
     */
    Long deleteByImei(String imei);

}
