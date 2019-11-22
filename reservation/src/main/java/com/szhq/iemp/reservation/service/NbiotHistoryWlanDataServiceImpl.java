package com.szhq.iemp.reservation.service;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.szhq.iemp.reservation.api.model.HistoryWlanData;
import com.szhq.iemp.reservation.api.service.NbiotHistoryWlanDataService;
import com.szhq.iemp.reservation.api.vo.query.DateQuery;
import com.szhq.iemp.reservation.repository.NbiotHistoryWlanDataRepository;
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
public class NbiotHistoryWlanDataServiceImpl implements NbiotHistoryWlanDataService {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keySpace;
    @Resource
    private NbiotHistoryWlanDataRepository nbiotHistoryWlanDataRepository;
    @Autowired
    private CassandraTemplate cassandraTemplate;

    @Override
    public List<HistoryWlanData> getData(String imei, Integer limit) {
        Select select = QueryBuilder.select().all().from(keySpace, "history_wlan_data")
                .where(QueryBuilder.eq("imei", imei)).orderBy(QueryBuilder.desc("ts")).limit(limit);
        log.info("select:" + select);
        List<HistoryWlanData> nbiotTrackerData = cassandraTemplate.select(select, HistoryWlanData.class);
        return nbiotTrackerData;
    }

    @Override
    public Integer deleteByImei(String imei) {
        return nbiotHistoryWlanDataRepository.deleteByImei(imei);
    }

    @Override
    public HistoryWlanData getDataByDateQuery(String imei, DateQuery query) {
        Select select = QueryBuilder.select().all().from(keySpace, "history_wlan_data")
                .where(QueryBuilder.eq("imei", imei)).and(QueryBuilder.gte("ts", query.getStartTime()))
                .and(QueryBuilder.lte("ts", query.getEndTime())).orderBy(QueryBuilder.asc("ts")).limit(1);
        List<HistoryWlanData> nbiotTrackerData = cassandraTemplate.select(select, HistoryWlanData.class);
        if(nbiotTrackerData != null && !nbiotTrackerData.isEmpty()){
            return nbiotTrackerData.get(0);
        }
        return null;
    }


}