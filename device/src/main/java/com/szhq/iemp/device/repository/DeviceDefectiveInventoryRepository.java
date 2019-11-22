package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceDefectiveInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDefectiveInventoryRepository extends JpaRepository<TdeviceDefectiveInventory, Integer>, JpaSpecificationExecutor<TdeviceDefectiveInventory> {

    @Query(value = "select * from t_device_defective_inventory where imei=?1", nativeQuery = true)
    public TdeviceDefectiveInventory findByImei(String imei);

    @Modifying
    @Query(value = "delete from t_device_defective_inventory where id=?1", nativeQuery = true)
    public Integer deleteByDefectiveId(Integer id);

    @Modifying
    @Query(value = "delete from t_device_defective_inventory where imei=?1", nativeQuery = true)
    public Integer deleteByImei(String imei);
}
