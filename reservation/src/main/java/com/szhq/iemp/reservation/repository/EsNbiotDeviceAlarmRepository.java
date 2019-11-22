package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsNbiotDeviceAlarmRepository extends ElasticsearchRepository<EsNbiotDeviceAlarm, String> {

}
