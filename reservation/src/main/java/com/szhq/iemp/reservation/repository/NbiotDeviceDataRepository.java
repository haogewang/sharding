package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.NbiotDeviceData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NbiotDeviceDataRepository extends CassandraRepository<NbiotDeviceData, String> {

    @Query(value = "select * from nbiot_history_data where imei= ?0 and ts >= ?1 and ts <= ?2",allowFiltering = true)
    public List<NbiotDeviceData> selectData(String imei, Date start, Date end);
    
    @Modifying
    @Query(value = "delete from nbiot_history_data where imei=?0",allowFiltering = true)
    public Integer deleteByImei(String imei);

}
