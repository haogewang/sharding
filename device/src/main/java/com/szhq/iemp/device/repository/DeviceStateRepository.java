package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceStateRepository extends JpaRepository<TdeviceState,String>,JpaSpecificationExecutor<TdeviceState> {

	@Query(value = "select * from nbiot_state where imei=?1", nativeQuery = true)
	public TdeviceState findByImei(String imei);
}
