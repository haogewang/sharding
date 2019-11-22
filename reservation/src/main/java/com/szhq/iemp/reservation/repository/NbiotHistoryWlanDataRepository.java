package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.HistoryWlanData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface NbiotHistoryWlanDataRepository extends CassandraRepository<HistoryWlanData, String> {

    @Modifying
    @Query(value = "delete from history_wlan_data where imei=?0", allowFiltering = true)
    public Integer deleteByImei(String imei);

}
