package com.szhq.iemp.reservation.api.service;


import com.szhq.iemp.reservation.api.model.HistoryWlanData;
import com.szhq.iemp.reservation.api.vo.query.DateQuery;

import java.util.List;

public interface NbiotHistoryWlanDataService {
	/**
	 * 取某一设备最近的指定数量点
	 */
	List<HistoryWlanData> getData(String imei, Integer limit);
	/**
	 * 根据imei删除cassandra历史数据
	 */
	Integer deleteByImei(String imei);
	/**
	 * 取某一时间段wlan点
	 */
	HistoryWlanData getDataByDateQuery(String imei, DateQuery query);
}