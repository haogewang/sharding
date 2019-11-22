package com.szhq.iemp.reservation.api.service;


import com.szhq.iemp.reservation.api.model.NbiotDeviceData;
import com.szhq.iemp.reservation.api.vo.query.DateQuery;

import java.util.List;

public interface NbiotDeviceDataService {
	/**
	 * 获取某一时间段imei的cassandra历史数据
	 */
	List<NbiotDeviceData> getData(String imei, Long start, Long end);

	/**
	 * 取某一设备最近的指定数量点
	 */
	List<NbiotDeviceData> getData(String imei, Integer limit);

	/**
	 * 查某一时间段轨迹点
	 */
	NbiotDeviceData getDataByDateQuery(String imei, DateQuery query);
	/**
	 * 根据imei删除cassandra历史数据
	 */
	Integer deleteByImei(String imei);

}