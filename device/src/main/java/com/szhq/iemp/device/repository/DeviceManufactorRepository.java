package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceManufactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceManufactorRepository extends JpaRepository<TdeviceManufactor, Integer>, JpaSpecificationExecutor<TdeviceManufactor> {

    @Modifying
    @Query(value = "delete from t_device_manufactor where id=?1", nativeQuery = true)
    public Integer deleteByManufactorId(Integer id);

    public TdeviceManufactor findByName(String name);

}
