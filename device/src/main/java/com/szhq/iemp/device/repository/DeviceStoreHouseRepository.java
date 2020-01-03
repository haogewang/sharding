package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceStoreHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceStoreHouseRepository extends JpaRepository<TdeviceStoreHouse, Integer>, JpaSpecificationExecutor<TdeviceStoreHouse> {

    public TdeviceStoreHouse findByName(String name);

    @Modifying
    @Query(value = "delete from t_device_storehouse where id in (?1)", nativeQuery = true)
    public Integer deleteByStoreIds(List<Integer> ids);

    @Modifying
    @Query(value = "delete from t_device_storehouse where id = ?1", nativeQuery = true)
    public Integer deleteByStoreId(Integer id);

    @Query(value = "select id from t_device_storehouse where operator_id = ?1", nativeQuery = true)
    public List<Integer> findStorIdsByOperatorId(Integer operatorId);

    @Modifying
    @Query(value = "delete from t_device_storehouse where operator_id in (?1)", nativeQuery = true)
    public int deleteByOperatorIds(List<Integer> operatorIds);

    @Query(value = "select * from t_device_storehouse where parent_id = ?1", nativeQuery = true)
    public List<TdeviceStoreHouse> findByParentId(Integer pId);

    /**
     * 激活设备数量统计
     */
    @Query(value = "select count(TDI.imei) as total, sum(if(TDI.is_active=1,1,0)) as active_count, sum(if(TDI.is_active=0,1,0)) as no_active_count, TDI.storehouse_id, TDS.name as storehouse_name, TDI.operator_name " +
                    "from t_device_inventory TDI left join t_device_storehouse TDS on TDI.storehouse_id = TDS.id " +
                    "group by TDI.storehouse_id", nativeQuery = true)
    public List<Map<String, Object>> deviceActiveStatistic();

    @Query(value = "select count(TDI.imei) as total, sum(if(TDI.is_active=1,1,0)) as active_count, sum(if(TDI.is_active=0,1,0)) as no_active_count, TDI.storehouse_id, TDS.name as storehouse_name, TDI.operator_name " +
                    "from t_device_inventory TDI left join t_device_storehouse TDS on TDI.storehouse_id = TDS.id " +
                    "where TDI.storehouse_id = ?1 and TDI.operator_id in (?2) group by TDI.storehouse_id", nativeQuery = true)
    public List<Map<String, Object>> deviceActiveStatistic(Integer storhouseId, List<Integer> operatorIds);

    @Query(value = "select count(TDI.imei) as total, sum(if(TDI.is_active=1,1,0)) as active_count, sum(if(TDI.is_active=0,1,0)) as no_active_count, TDI.storehouse_id, TDS.name as storehouse_name, TDI.operator_name " +
                    "from t_device_inventory TDI left join t_device_storehouse TDS on TDI.storehouse_id = TDS.id " +
                    "where TDI.operator_id in (?1) group by TDI.storehouse_id", nativeQuery = true)
    public List<Map<String, Object>> deviceActiveStatistic(List<Integer> operatorIds);

    @Query(value = "select * from t_device_storehouse where operator_id = ?1", nativeQuery = true)
    List<TdeviceStoreHouse> findAllStoresByOperatorId(Integer operatorId);

    @Query(value = "select * from t_device_storehouse where id in (?1)", nativeQuery = true)
    List<TdeviceStoreHouse> findByIds(List<Integer> storehouseIds);
}
