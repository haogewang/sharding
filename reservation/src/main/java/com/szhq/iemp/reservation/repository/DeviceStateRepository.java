package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TdeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceStateRepository extends JpaRepository<TdeviceState, String>, JpaSpecificationExecutor<TdeviceState> {

    @Modifying
    @Query(value = "delete from nbiot_state where imei=?1", nativeQuery = true)
    public Integer deleteByImei(String imei);
}
