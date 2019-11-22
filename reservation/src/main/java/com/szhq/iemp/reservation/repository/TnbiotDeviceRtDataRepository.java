package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.NbiotDeviceRtData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TnbiotDeviceRtDataRepository extends JpaRepository<NbiotDeviceRtData, Integer>, JpaSpecificationExecutor<NbiotDeviceRtData> {

    @Query(value = "select * from nbiot_device_rt_data where imei = ?1 order by ts desc limit 1", nativeQuery = true)
    public NbiotDeviceRtData findByImei(String imei);

    @Query(value = "select * from nbiot_device_rt_data where imei = ?1 and operator_id in (?2) order by ts desc limit 1", nativeQuery = true)
    public NbiotDeviceRtData findByImei(String imei, List<Integer> ids);

    //and ts > (UNIX_TIMESTAMP(CURDATE())*1000)
    @Query(value = "select * from nbiot_device_rt_data where json_extract(gps,'$.lat') >= :lowerLeftLat and json_extract(gps,'$.lat') <= :upperRightLat and json_extract(gps,'$.lon')>= :lowerLeftLon and json_extract(gps,'$.lon') <= :upperRightLon", nativeQuery = true)
    public List<NbiotDeviceRtData> selectData(@Param(value = "lowerLeftLat") double lowerLeftLat, @Param(value = "lowerLeftLon") double lowerLeftLon, @Param(value = "upperRightLat") double upperRightLat, @Param(value = "upperRightLon") double upperRightLon);

    @Query(value = "select * from nbiot_device_rt_data where json_extract(gps,'$.lat') >= :lowerLeftLat and json_extract(gps,'$.lat') <= :upperRightLat and json_extract(gps,'$.lon')>= :lowerLeftLon and json_extract(gps,'$.lon') <= :upperRightLon and operator_id in (:ids)", nativeQuery = true)
    public List<NbiotDeviceRtData> selectData(@Param(value = "lowerLeftLat") double lowerLeftLat, @Param(value = "lowerLeftLon") double lowerLeftLon, @Param(value = "upperRightLat") double upperRightLat, @Param(value = "upperRightLon") double upperRightLon, @Param(value = "ids") List<Integer> ids);

    @Modifying
    @Query(value = "delete from nbiot_device_rt_data where imei = ?1", nativeQuery = true)
    public Integer deleteByImei(String imei);


    @Query(value = "select imei, gps, ts from nbiot_device_rt_data where imei in (?1)", nativeQuery = true)
    public List<Map<String, Object>> findByImeiIn(List<String> imeis);

    @Query(value = "select * from nbiot_device_rt_data where imei in (?1)", nativeQuery = true)
    public List<NbiotDeviceRtData> findByImeis(List<String> imeis);
}
