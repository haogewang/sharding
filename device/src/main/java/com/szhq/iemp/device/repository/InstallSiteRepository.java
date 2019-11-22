package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.TinstallSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface InstallSiteRepository extends JpaRepository<TinstallSite, Integer>, JpaSpecificationExecutor<TinstallSite> {

    public TinstallSite findByName(String name);

    @Query(value = "select count(1) from t_device_inventory where install_site_id =?1", nativeQuery = true)
    public Integer getTotalDeviceBySiteId(Integer id);

    @Query(value = "select count(1) from t_electrmobile where install_site_id =?1", nativeQuery = true)
    public Integer getEquipDeviceBySiteId(Integer id);

    @Query(value = "select count(1) from nbiot_device_rt_data where imei in (select imei from t_electrmobile where install_site_id = ?1)", nativeQuery = true)
    public Integer getOnlineEquipDeviceBySiteId(Integer id);

    @Query(value = "select count(1) from t_electrmobile where date(create_time) = curdate() and install_site_id =?1", nativeQuery = true)
    public Integer getTodayInstalledCountBySiteId(Integer id);

    @Query(value = "select count(1) from nbiot_device_rt_data where imei in (select imei from t_electrmobile where date(create_time) = curdate() and install_site_id =?1)", nativeQuery = true)
    public Integer getTodayOnlineCountBySiteId(Integer id);

    @Query(value = "select AVG(install_site_score) from t_install_site_score where install_site_id =?1", nativeQuery = true)
    public Double getScoreBySiteId(Integer id);

    @Query(value = "select * from t_install_site where city_id =?1 and status = ?2", nativeQuery = true)
    public List<TinstallSite> getSitesByCityId(Integer id, Boolean status);

    @Query(value = "select * from t_install_site where region_id =?1 and status = ?2", nativeQuery = true)
    public List<TinstallSite> getSitesByRegionId(Integer id, Boolean status);

    @Query(value="select NE.create_time as create_time, count(NE.imei) as total_installed, count(NDRD.imei) as onlineCount" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI" +
            " on DI.imei=NE.imei" +
            " inner join t_install_site NRS" +
            " on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where datediff(now(),NE.create_time) <= ?1 and NRS.install_site_id = ?2 group by day(NE.create_time)", nativeQuery = true)
    List<Map<String, Object>> countByOffsetAndInstallSiteId(Integer offset, Integer installSiteId);

    @Modifying
    @Query(value = "delete from t_install_site where install_site_id=?1", nativeQuery = true)
    public Integer deleteByInstallSiteId(Integer id);


    @Query(value = "select install_site_id, name, operator_id, status, region_id, region_name, lat, lon, address from t_install_site where region_id like ?1%", nativeQuery = true)
    public List<Map<String, Object>> findByAdressRegionIdLike(String regionId);

    @Query(value = "select cdi.createTime, sum(cdi.deviceCount) as totalInstalled, cdi.installSiteName, cdi.installSiteId "
            + "from (select TE.create_time as createTime,count(TE.imei) as deviceCount, TIS.name as installSiteName, TIS.install_site_id as installSiteId " +
            "from t_electrmobile TE left join t_install_site TIS " +
            "on TE.install_site_id=TIS.install_site_id " +
            "where datediff(now(),TE.create_time) <= ?1 group by day(TE.create_time),TIS.install_site_id) cdi group by cdi.installSiteId order by sum(cdi.deviceCount) desc", nativeQuery = true)
    public List<Map<String, Object>> installSiteOrder(Integer offset);

    @Query(value = "select cdi.createTime, sum(cdi.deviceCount) as totalInstalled, cdi.installSiteName, cdi.installSiteId "
            + "from (select TE.create_time as createTime,count(TE.imei) as deviceCount, TIS.name as installSiteName, TIS.install_site_id as installSiteId " +
            "from t_electrmobile TE left join t_install_site TIS " +
            "on TE.install_site_id=TIS.install_site_id " +
            "where datediff(now(),TE.create_time) <= ?1 and TIS.operator_id in(?2) group by day(TE.create_time),TIS.install_site_id) cdi group by cdi.installSiteId order by sum(cdi.deviceCount) desc", nativeQuery = true)
    public List<Map<String, Object>> installSiteOrder(Integer offset, List<Integer> operatorIds);

    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NE.create_time between  ?1 and ?2 and NRS.name like %?3% " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByCondition(Date start, Date end, String installSiteName);
    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NE.create_time between ?1 and ?2 and NRS.name like %?3% and DI.operator_id in (?4)" +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByCondition(Date start, Date end, String installSiteName, List<Integer> operatorIds);

    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NRS.name like %?1% " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByInstallSiteName(String installSiteName);
    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NRS.name like %?1% and DI.operator_id in (?2) " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByInstallSiteName(String installSiteName, List<Integer> operatorIds);

    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NE.create_time between  ?1 and ?2 " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByDate(Date start, Date end);
    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei" +
            " where NE.create_time between  ?1 and ?2 and DI.operator_id in (?3)" +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countByDate(Date start, Date end, List<Integer> operatorIds);

    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countAll();
    @Query(value="select DI.install_site_id as install_site_id, NRS.name as site_name, count(NE.imei) as total_installed, sum(if(DI.isp='CT',1,0)) as ct_installed, sum(if(DI.isp='CMCC',1,0)) as cmcc_installed, sum(if(DI.isp='CUCC',1,0)) as cucc_installed," +
            " sum(if(NDRD.imei is not null,1,0)) as total_online, sum(if(NDRD.imei is not null and DI.isp='CT',1,0)) as ct_online, sum(if(NDRD.imei is not null and DI.isp='CMCC',1,0)) as cmcc_online, sum(if(NDRD.imei is not null and DI.isp='CUCC',1,0)) as cucc_online" +
            " from t_electrmobile NE" +
            " inner join t_device_inventory DI on DI.imei=NE.imei" +
            " inner join t_install_site NRS on NRS.install_site_id=DI.install_site_id" +
            " left join nbiot_device_rt_data NDRD on NDRD.imei=DI.imei where DI.operator_id in (?1) " +
            " group by install_site_id", nativeQuery = true)
    List<Map<String, Object>> countAll(List<Integer> operatorIds);

    @Query(value = "select TIS.install_site_id, TIS.name, U.id, U.name as username, count(R.imei) as installedCount from t_registration R left join user U on U.id=R.create_by inner join t_install_site TIS on U.install_site_id=TIS.install_site_id  where TIS.operator_id in (?1) and datediff(now(),R.create_time) <= ?2 group by R.create_by", nativeQuery = true)
    List<Map<String, Object>> getSiteInstalledCount(List<Integer> operatorIds, Integer offset);

    @Query(value = "select TIS.install_site_id, TIS.name, U.id, U.name as username, count(R.imei) as installedCount from t_registration R left join user U on U.id=R.create_by inner join t_install_site TIS on U.install_site_id=TIS.install_site_id  where datediff(now(),R.create_time) <= ?1 group by R.create_by", nativeQuery = true)
    List<Map<String, Object>> getSiteInstalledCount(Integer offset);
}
