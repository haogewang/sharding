package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.NbiotDeviceRtAlarm;

import java.util.List;

public interface NbiotDeviceAlarmRtDataService {
	/**
	 * 获取屏幕上实时告警点
	 */
	 List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);

    /**
     *通过type过滤实时告警
     */
     List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);
	/**
	 * 通过运营公司id获取屏幕上实时告警点
	 */
	 List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(List<Integer> operatorIds, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);
	/**
	 *通过type及运营公司id过滤实时告警
	 */
	 List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, List<Integer> operatorIds, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);

	 Integer deleteByImei(String imei);
}
