package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.NbiotDeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NbiotDeviceInfoRepository extends JpaRepository<NbiotDeviceInfo, String>, JpaSpecificationExecutor<NbiotDeviceInfo> {


    @Query(value = "delete from nbiot_device_info where imei = ?1", nativeQuery = true)
    @Modifying
    public int deleteByImei(String oldImei);

    @Query(value = "update nbiot_device_info set operator_id = ?2 where imei = ?1", nativeQuery = true)
    @Modifying
    public int updateOperatorByImei(String imei, Integer id);
}
