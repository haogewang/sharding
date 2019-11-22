package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.NbiotDeviceRtAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TnbiotDeviceRtAlarmRepository extends JpaRepository<NbiotDeviceRtAlarm,Integer>,JpaSpecificationExecutor<NbiotDeviceRtAlarm> {

	@Query(value = "select * from nbiot_device_rt_alarm where imei = ?1", nativeQuery = true)
	public NbiotDeviceRtAlarm findByImei(String imei);
	
	@Modifying
	@Query(value = "delete from nbiot_device_rt_alarm where imei = ?1", nativeQuery = true)
	public Integer deleteByImei(String imei);
	
	@Query(value = "select * from nbiot_device_rt_alarm where lat >= ?1 and lat <= ?3 and lon>= ?2 and lon<=?4 and to_days(time)=to_days(now())", nativeQuery = true)
	public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);

	@Query(value = "select * from nbiot_device_rt_alarm where dev_type = ?1 and lat >= ?2 and lat <= ?4 and lon>= ?3 and lon<=?5 and to_days(time)=to_days(now())", nativeQuery = true)
	public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);

	@Query(value = "select * from nbiot_device_rt_alarm where lat >= ?2 and lat <= ?4 and lon>= ?3 and lon<=?5 and operator_id in(?1) and to_days(time)=to_days(now())", nativeQuery = true)
	public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(List<Integer> operatorIds, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);

	@Query(value = "select * from nbiot_device_rt_alarm where dev_type = ?1 and lat >= ?3 and lat <= ?5 and lon>= ?4 and lon<=?6 and operator_id in(?2) and to_days(time)=to_days(now())", nativeQuery = true)
	public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, List<Integer> operatorIds, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon);
}
