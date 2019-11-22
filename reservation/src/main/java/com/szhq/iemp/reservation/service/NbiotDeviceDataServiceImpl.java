package com.szhq.iemp.reservation.service;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.ListTranscoder;
import com.szhq.iemp.reservation.api.model.NbiotDeviceData;
import com.szhq.iemp.reservation.api.service.NbiotDeviceDataService;
import com.szhq.iemp.reservation.api.vo.query.DateQuery;
import com.szhq.iemp.reservation.repository.NbiotDeviceDataRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
@Slf4j
public class NbiotDeviceDataServiceImpl implements NbiotDeviceDataService {

	@Value("${spring.data.cassandra.keyspace-name}")
	private String keySpace;
	@Resource
	private NbiotDeviceDataRepository nbiotTrackerDataRepository;
	@Resource(name = "primaryRedisUtil")
	private RedisUtil redisUtil;
	@Autowired
	private CassandraTemplate cassandraTemplate;
	
    @SuppressWarnings("unchecked")
	@Override
    public List<NbiotDeviceData> getData(String imei, Long start, Long end) {
    	Object datas = redisUtil.get(CommonConstant.IEMP_NBIOT_TRACKER_IMEI  + imei + start + end);
		ListTranscoder<NbiotDeviceData> listTranscoder = new ListTranscoder<NbiotDeviceData>();
		if(datas == null) {
			Select select = QueryBuilder.select().all().from(keySpace, "nbiot_history_data")
					.where(QueryBuilder.eq("imei", imei)).and(QueryBuilder.gte("ts", start))
					.and(QueryBuilder.lte("ts", end)).orderBy(QueryBuilder.asc("ts"));
			log.info("select:" + select);
			List<NbiotDeviceData> nbiotTrackerData = cassandraTemplate.select(select, NbiotDeviceData.class);
			if(nbiotTrackerData != null) {
				redisUtil.set(CommonConstant.IEMP_NBIOT_TRACKER_IMEI  + imei + start + end, listTranscoder.serialize(nbiotTrackerData), 86400);
			}
			return nbiotTrackerData;
		}
		Object o = listTranscoder.deserialize((String)datas);
		log.info("get cassandra nbiotDeviceData from redis. imei:" + imei);
		return (List<NbiotDeviceData>)o;
		
//    	Date startTime = new Date(start);
//    	Date endTime = new Date(end);
//    	log.info("startTime:" + startTime + ",endTime:" + endTime);
//        List<NbiotDeviceData> nbiotTrackerData = nbiotTrackerDataRepository.selectData(imei, startTime, endTime);
//        return nbiotTrackerData;
    }

	@Override
	public List<NbiotDeviceData> getData(String imei, Integer limit) {
		Select select = QueryBuilder.select().all().from(keySpace, "nbiot_history_data")
				.where(QueryBuilder.eq("imei", imei)).orderBy(QueryBuilder.desc("ts")).limit(limit);
		log.info("select:" + select);
		List<NbiotDeviceData> nbiotTrackerData = cassandraTemplate.select(select, NbiotDeviceData.class);
		return nbiotTrackerData;
	}

	@Override
	public NbiotDeviceData getDataByDateQuery(String imei, DateQuery query) {
		Select select = QueryBuilder.select().all().from(keySpace, "nbiot_history_data")
				.where(QueryBuilder.eq("imei", imei)).and(QueryBuilder.gte("ts", query.getStartTime()))
				.and(QueryBuilder.lte("ts", query.getEndTime())).orderBy(QueryBuilder.asc("ts")).limit(1);
		List<NbiotDeviceData> nbiotTrackerData = cassandraTemplate.select(select, NbiotDeviceData.class);
		if(nbiotTrackerData != null && !nbiotTrackerData.isEmpty()){
			return nbiotTrackerData.get(0);
		}
		return null;
	}

	@Override
	public Integer deleteByImei(String imei) {
		return nbiotTrackerDataRepository.deleteByImei(imei);
	}
    
    
}