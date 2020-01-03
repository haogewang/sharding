package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TdeviceStoreHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceStoreHouseRepository extends JpaRepository<TdeviceStoreHouse, Integer>, JpaSpecificationExecutor<TdeviceStoreHouse> {


    @Query(value = "select * from t_device_storehouse where id = ?1", nativeQuery = true)
    public TdeviceStoreHouse findByStoreId(Integer id);

}
