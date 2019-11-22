package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.model.Tgroup;
import com.szhq.iemp.device.api.model.Toperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectrmobileRepository extends JpaRepository<Telectrmobile, Long>, JpaSpecificationExecutor<Telectrmobile> {

    @Query(value = "select count(1) from t_electrmobile where operator_id =?1", nativeQuery = true)
    public Integer getDeviceNumberByOperatorId(Integer operatorId);

    @Query(value = "select count(1) from t_electrmobile where imei =?1", nativeQuery = true)
    public Integer countByImei(String imei);

    @Query(value = "select name from t_electrmobile_type where type_id =?1", nativeQuery = true)
    String getTypeById(Integer typeId);

    @Query(value = "select name from t_electrmobile_vendor where vendor_id =?1", nativeQuery = true)
    String getVendorById(Integer vendorId);

    @Query(value = "select imei from t_electrmobile where electrmobile_id in (?1)", nativeQuery = true)
    List<String> findImeisByElecIds(List<Long> elecIds);

    @Query(value = "select * from t_electrmobile where imei in (?1)", nativeQuery = true)
    List<Telectrmobile> findByImeis(List<String> imeis);

    @Modifying
    @Query(value = "update t_electrmobile set storehouse_name = ?1 where storehouse_id = ?2", nativeQuery = true)
    Integer updateStoreNameByStorehouseId(String name, Integer id);

    @Modifying
    @Query(value = "update t_electrmobile set install_site_name = ?1 where install_site_id = ?2", nativeQuery = true)
    Integer updateSiteNameBySiteId(String name, Integer installSiteId);

    @Query(value = "select count(1) from t_electrmobile where group_id =?1", nativeQuery = true)
    Integer countByGroupId(Integer id);

    @Modifying
    @Query(value = "update t_electrmobile set group_id = null where imei in (?1)", nativeQuery = true)
    Integer removeGroupByImeis(List<String> imeis);
}
