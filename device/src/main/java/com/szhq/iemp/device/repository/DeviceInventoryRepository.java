package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceInventory;
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
public interface DeviceInventoryRepository extends JpaRepository<TdeviceInventory, String>, JpaSpecificationExecutor<TdeviceInventory> {


    public TdeviceInventory findByImei(String imei);

    public List<TdeviceInventory> findByImeiIn(List<String> imeiList);

    @Query(value = "select distinct(operator_id) from t_device_inventory where box_number in (?1)", nativeQuery = true)
    public List<Integer> getOperatorIdsByBoxNumbers(List<String> boxNumbers);

    @Query(value = "select distinct(operator_id) from t_device_inventory where imei in (?1)", nativeQuery = true)
    public List<Integer> getOperatorIdsByImeis(List<String> imeis);

    @Modifying
    @Query(value = "delete from t_device_inventory where imei = ?1", nativeQuery = true)
    public Integer deleteByImei(String imei);

    @Query(value = "select * from t_device_inventory where box_number = ?1", nativeQuery = true)
    public List<TdeviceInventory> findByBoxNumber(String boxNumber);

    @Query(value = "select * from t_device_inventory where box_number in (?1)", nativeQuery = true)
    public List<TdeviceInventory> findByBoxNumberIn(List<String> boxNumbers);

    /**
     * 入库统计
     */
    @Query(value = "select count(distinct(box_number)) as box_count,count(imei) as imei_count, DATE_FORMAT(put_storage_time,'%Y-%m-%d') as put_storage_time,sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count from t_device_inventory where if(?1 is null or ?1='', 1=1, devtype=?1) and storehouse_id <>1 group by DATE_FORMAT(put_storage_time,'%Y-%m-%d')", nativeQuery = true)
    public List<Map<String, Object>> putStorageStatistic(String devType, Pageable pageable);

    @Query(value = "select count(distinct(box_number)) as box_count,count(imei) as imei_count, DATE_FORMAT(put_storage_time,'%Y-%m-%d') as put_storage_time,sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count from t_device_inventory where operator_id in (?1) and if(?2 is null or ?2='', 1=1, devtype=?2) and storehouse_id <>1 group by DATE_FORMAT(put_storage_time,'%Y-%m-%d')", nativeQuery = true)
    public List<Map<String, Object>> putStorageStatistic(List<Integer> operatorIds, String devType, Pageable pageable);

    @Query(value = "select count(distinct(box_number)) as box_count,count(imei) as imei_count, DATE_FORMAT(put_storage_time,'%Y-%m-%d') as put_storage_time,sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count from t_device_inventory where put_storage_time between ?1 and ?2 and if(?3 is null or ?3='', 1=1, devtype=?3) and storehouse_id <>1 group by DATE_FORMAT(put_storage_time,'%Y-%m-%d')", nativeQuery = true)
    public List<Map<String, Object>> putStorageStatistic(Date startTime, Date endTime, String devType, Pageable pageable);

    @Query(value = "select count(distinct(box_number)) as box_count,count(imei) as imei_count, DATE_FORMAT(put_storage_time,'%Y-%m-%d') as put_storage_time,sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count from t_device_inventory where put_storage_time between ?1 and ?2 and t.operator_id in (?3) and if(?4 is null or ?4='', 1=1, devtype=?4) and storehouse_id <>1 group by DATE_FORMAT(put_storage_time,'%Y-%m-%d')", nativeQuery = true)
    public List<Map<String, Object>> putStorageStatistic(Date startTime, Date endTime, List<Integer> operatorIds, String devType, Pageable pageable);

    /**
     * 根据入库时间获取入库详情
     */
    @Query(value = "select put_storage_user_id, box_number, count(imei) as total_count, isp from t_device_inventory where put_storage_time between ?1 and ?2 group by box_number", nativeQuery = true)
    public List<Map<String, Object>> getBoxNumberByPutStorageTime(Date start, Date end);

    @Query(value = "select put_storage_user_id, box_number, count(imei) as total_count, isp from t_device_inventory where put_storage_time between ?1 and ?2 and operator_id in (?3) group by box_number", nativeQuery = true)
    public List<Map<String, Object>> getBoxNumberByPutStorageTime(Date start, Date end, List<Integer> operatorIds);

    /**
     *获取可退库的箱号
     */
    @Query(value = "select distinct(box_number), count(imei) as total_count, isp from t_device_inventory where install_site_id is not null and devstate <> 1 group by box_number", nativeQuery = true)
    public List<Map<String, Object>> getBackOffBoxNumbers();

    @Query(value = "select distinct(box_number), count(imei) as total_count, isp from t_device_inventory where install_site_id is not null and devstate <> 1 and operator_id in (?1) group by box_number", nativeQuery = true)
    public List<Map<String, Object>> getBackOffBoxNumbers(List<Integer> operatorIds);

    @Query(value = "select imei from t_device_inventory where install_site_id is not null and devstate <> 1 and storehouse_id <> 1 and box_number in (?1)", nativeQuery = true)
    public List<String> getBackOffImeisByBoxNumbers(List<String> boxNumber);
    /**
     * 根据箱号获取安装点及设备数量
     */
    @Query(value = "select install_site_id, count(imei) from t_device_inventory where box_number=?1 group by install_site_id", nativeQuery = true)
    public List<Map<String, Object>> getInstallSiteAndCountByBoxNumber(String boxNumber);

    /**
     * 根据运营商获取未分配设备数量
     */
    @Query(value = "select count(imei) as counts, isp from t_device_inventory where operator_id in (?1) and storehouse_id <> 1 and install_site_id is null group by isp", nativeQuery = true)
    public List<Map<String, Object>> getUndispacheDeviceCountOfIsp(List<Integer> operatorIds);

    @Query(value = "select count(imei) as counts, isp from t_device_inventory where storehouse_id <> 1 and install_site_id is null group by isp", nativeQuery = true)
    public List<Map<String, Object>> getUndispacheDeviceCountOfIsp();

    @Query(value = "select count(1) from t_device_inventory where install_site_id = ?1", nativeQuery = true)
    public Integer getDeviceNumByInstallSiteId(Integer id);

    @Query(value = "select count(1) from t_device_inventory where operator_id in (?1)", nativeQuery = true)
    public Long getDeviceNumByOperatorIds(List<Integer> ids);

    /**
     * 分配统计
     */
    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where if(?1 is null or ?1='', 1=1, devtype=?1) group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where operator_id in (?1) and if(?2 is null or ?2='', 1=1, devtype=?2) group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(List<Integer> operatorIds, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where install_site_name like '%?1%' and if(?2 is null or ?2='', 1=1, devtype=?2) group by day(dispatch_time), install_site_id ", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(String installSiteName, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where install_site_name like '%?1%' and operator_id in (?2) and if(?3 is null or ?3='', 1=1, devtype=?3) group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(String installSiteName, List<Integer> operatorIds, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where dispatch_time between ?1 and ?2 and if(?3 is null or ?3='', 1=1, devtype=?3) group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(Date startTime, Date endTime, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count, "
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where dispatch_time between ?1 and ?2 and operator_id in (?3) and if(?4 is null or ?4='', 1=1, devtype=?4) group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(Date startTime, Date endTime, List<Integer> operatorIds, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where install_site_name like '%?1%' and dispatch_time between ?2 and ?3 and if(?4 is null or ?4='', 1=1, devtype=?4)"
            + "group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(String installSiteName, Date startTime, Date endTime, String devType, Pageable pageable);

    @Query(value = "select install_site_id, install_site_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, sum(if(isp='CMCC', 1,0)) as cmcc_count,"
            + "sum(if(isp='CUCC', 1,0)) as cucc_count, dispatch_time from t_device_inventory where install_site_name like '%?1%' and dispatch_time between ?2 and ?3 and operator_id in(?4) and if(?5 is null or ?5='', 1=1, devtype=?5)"
            + "group by day(dispatch_time), install_site_id", nativeQuery = true)
    public List<Map<String, Object>> dispatchStatistic(String installSiteName, Date startTime, Date endTime, List<Integer> operatorIds, String devType, Pageable pageable);

    /**
     * 历史安装异常数统计
     */
    @Query(value = "select TIS.name, count(TE.imei) as totals, sum(if(TDI.isp='CT', 1,0)) as CTcount, sum(if(TDI.isp='CMCC', 1,0)) as CMCCcount from t_electrmobile TE left join nbiot_device_rt_data NDRT on TE.imei=NDRT.imei inner join t_device_inventory TDI on TDI.imei=TE.imei left join t_install_site TIS on TIS.install_site_id=TDI.install_site_id where NDRT.imei is null group by TDI.install_site_id order by totals desc", nativeQuery = true)
    public List<Map<String, Object>> countHistoryInstalledUnormalCount();

    @Query(value = "select TIS.name, count(TE.imei) as totals, sum(if(TDI.isp='CT', 1,0)) as CTcount, sum(if(TDI.isp='CMCC', 1,0)) as CMCCcount from t_electrmobile TE left join nbiot_device_rt_data NDRT on TE.imei=NDRT.imei inner join t_device_inventory TDI on TDI.imei=TE.imei left join t_install_site TIS on TIS.install_site_id=TDI.install_site_id where NDRT.imei is null and TDI.operator_id in (?1) group by TDI.install_site_id order by totals desc", nativeQuery = true)
    public List<Map<String, Object>> countHistoryInstalledUnormalCount(List<Integer> operatorIds);

    /**
     * 历史安装统计（每天/月）
     */
    @Query(value = "select e.create_time as time, count(e.imei) as counts from t_electrmobile e where datediff(now(),e.create_time) <= ?1 group by day(e.create_time)", nativeQuery = true)
    public List<Map<String, Object>> countHistoryInstalledByOffset(Integer offset);

    @Query(value = "select e.create_time as time, count(e.imei) as counts from t_electrmobile e where datediff(now(),e.create_time) <= ?1 and e.operator_id in (?2) group by day(e.create_time)", nativeQuery = true)
    public List<Map<String, Object>> countHistoryInstalledByOffset(Integer offset, List<Integer> operatorIds);

    @Query(value = "select count(1) from t_device_inventory where group_id = ?1", nativeQuery = true)
    public Integer countByGroupId(Integer groupId);

    @Query(value = "select * from t_device_inventory where group_id = ?1", nativeQuery = true)
    public List<TdeviceInventory> findByGroupId(Integer groupId);

    @Query(value = "select count(1) from t_device_inventory where storehouse_id = ?1", nativeQuery = true)
    public Long countByStoreHouseId(Integer storehouseId);

    @Query(value = "select count(1) from t_device_inventory where storehouse_id in (?1)", nativeQuery = true)
    Long countByStoreHouseIds(List<Integer> subStoreIds);

    @Query(value = "select imei, operator_id from t_device_inventory", nativeQuery = true)
    List<Map<String, Object>> findOperatorIdAndImeiRelations();

    @Query(value = "select TDI.box_number, count(TDI.imei) as totalCount, isp from t_device_inventory TDI left join t_device_storehouse TDS "
            + "on TDI.storehouse_id=TDS.id where TDI.install_site_id is null and TDS.id <> 1 group by TDI.box_number", nativeQuery = true)
    public List<Map<String, Object>> getDispatchBoxNumbers();

    @Query(value = "select TDI.box_number, count(TDI.imei) as totalCount, isp from t_device_inventory TDI left join t_device_storehouse TDS "
            + "on TDI.storehouse_id=TDS.id where TDI.install_site_id is null and TDS.id <> 1 and TDI.operator_id in (?1) group by TDI.box_number", nativeQuery = true)
    public List<Map<String, Object>> getDispatchBoxNumbers(List<Integer> operatorIds);

    @Modifying
    @Query(value = "update t_device_inventory set storehouse_name = ?1 where storehouse_id = ?2", nativeQuery = true)
    Integer updateStoreNameByStorehouseId(String storeName, Integer id);

    @Modifying
    @Query(value = "update t_device_inventory set install_site_name = ?1 where install_site_id = ?2", nativeQuery = true)
    Integer updateSiteNameBySiteId(String name, Integer installSiteId);

    @Query(value = "select count(1) from t_device_inventory where operator_id in (?1) and is_active = false", nativeQuery = true)
    Integer countUnActiveByOperatorIds(List<Integer> operatorIds);

    @Modifying
    @Query(value="update t_device_inventory set group_id = null where imei in (?1)", nativeQuery = true)
    public Integer removeGroupByImeis(List<String> imeis);
    /**
     * 根据运营公司统计库存(只统计有激活属性的)
     */
    @Query(value = "select count(1) from t_device_inventory where operator_id in (?1) and is_active = false", nativeQuery = true)
    Integer countStoreCountByOperatorIds(List<Integer> operatorIds);
    /**
     * 根据运营公司统计销售数（包括安装及解绑设备）
     */
    @Query(value = "select count(1) from t_device_inventory where operator_id in (?1) and devstate <> 0 and storehouse_id <> 1" +
            " and imei not in (select imei from t_activator where operator_id in (?1))", nativeQuery = true)
    Integer countSellCountByOperatorIds(List<Integer> operatorIds);

    /**
     * 根据仓库Id统计未销售数
     */
    @Query(value = "select imei from t_device_inventory where storehouse_id =?1 and devstate <> 1", nativeQuery = true)
    List<String> getUnSellDevicesByStorehouseId(Integer id);
    /**
     * 获取可入库的箱子
     */
    @Query(value = "select * from t_device_inventory where if(?1 is null or ?1='', 1=1, box_number = ?1) " +
                    "and if(?2 is null or ?2='', 1=1, storehouse_name like %?2%) and if(?3 is null or ?3='', 1=1, operator_name like %?3%) " +
                    "and operator_id in (?4) and if(?5 = 1 and ?5 is not null, storehouse_id = 1, storehouse_id <> 1) and box_number is not null group by box_number", nativeQuery = true)
    List<TdeviceInventory> getBoxNumbersOfPutStorage(String boxNumber, String storehouseName, String operatorName, List<Integer> operatorIds, Integer isPutStorage, Pageable pageable);

    @Query(value = "select * from t_device_inventory where if(?1 is null or ?1='', 1=1, box_number = ?1) " +
            "and if(?2 is null or ?2='', 1=1, operator_name like %?2%) " +
            "and operator_id in (?3) and box_number is not null group by box_number", nativeQuery = true)
    List<TdeviceInventory> getBoxNumbersOfPutStorage(String boxNumber, String operatorName, List<Integer> operatorIds, Pageable pageable);

    @Query(value = "select * from t_device_inventory where if(?1 is null or ?1='', 1=1, box_number = ?1) " +
                    "and if(?2 is null or ?2='', 1=1, operator_name like %?2%) and if(?3 = 1, storehouse_id = 1, storehouse_id <> 1) and box_number is not null group by box_number", nativeQuery = true)
    List<TdeviceInventory> getBoxNumbersOfPutStorage(String boxNumber, String operatorName, Integer isPutStorage, Pageable pageable);

    @Query(value = "select * from t_device_inventory where if(?1 is null or ?1='', 1=1, box_number = ?1) " +
            "and if(?2 is null or ?2='', 1=1, operator_name like %?2%) and box_number is not null group by box_number", nativeQuery = true)
    List<TdeviceInventory> getBoxNumbersOfPutStorage(String boxNumber, String operatorName, Pageable pageable);

    @Query(value = "select imei from t_device_inventory where group_id = ?1", nativeQuery = true)
    List<String> getImeisByGroupId(Integer groupId);

    @Query(value = "select U.name,R.create_time from t_registration R left join user U on R.create_by=U.id where R.imei = ?1", nativeQuery = true)
    List<Map<String, Object>> getInstalledWorkerByImei(String imei);

    @Query(value = "select R.imei, U.name,R.create_time from t_registration R left join user U on R.create_by=U.id where R.imei in (?1)", nativeQuery = true)
    List<Map<String, Object>> getInstalledWorkerByImeis(List<String> imeis);

    @Query(value = "select * from t_device_inventory where install_site_id = ?1", nativeQuery = true)
    List<TdeviceInventory> findAllByInstallSiteId(Integer siteId);

    @Modifying
    @Query(value="update t_device_inventory set is_active = ?2 where imei in (?1)", nativeQuery = true)
    Integer updateActiveStateByImeis(List<String> imeis, Boolean status);
}
