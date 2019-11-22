package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TdeviceInventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface DeviceInventoryRepository extends JpaRepository<TdeviceInventory,Integer>,JpaSpecificationExecutor<TdeviceInventory> {

	@Query(value="select * from t_device_inventory where imei = ?1 and install_site_id <> ''", nativeQuery = true)
	public TdeviceInventory findByImeiAndInstallSiteIdIsNotNull(String imei);
	
	public TdeviceInventory findByImei(String imei);

	@Query(value="select * from t_device_inventory where iot_device_id = ?1", nativeQuery = true)
	public TdeviceInventory findByIotDeviceId(String iotDeviceId);	
	
	@Modifying
	@Query(value="delete from t_device_inventory where imei = ?1", nativeQuery = true)
	public Integer deleteByImei(String imei);


}
