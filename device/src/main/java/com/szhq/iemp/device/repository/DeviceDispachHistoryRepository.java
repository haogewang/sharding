package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TdeviceDispatchHistory;
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
public interface DeviceDispachHistoryRepository extends JpaRepository<TdeviceDispatchHistory, Integer>, JpaSpecificationExecutor<TdeviceDispatchHistory> {

    public TdeviceDispatchHistory findByImei(String imei);

    @Modifying
    @Query(value = "delete from t_device_dispatch_history where id=?1", nativeQuery = true)
    public Integer deleteByHistoryId(Integer id);

    /**
     * 退货统计
     */
    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and operator_id in (:operatorIds) group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(@Param("operatorIds") List<Integer> operatorIds, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and old_site_name like %?1% group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(String installSiteName, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and old_site_name like %?1% and operator_id in (?2) group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(String installSiteName, List<Integer> operatorIds, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and create_time between ?1 and ?2 group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(Date startTime, Date endTime, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and create_time between ?1 and ?2 and operator_id in (?3) group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(Date startTime, Date endTime, List<Integer> operatorIds, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and old_site_name like '%?1%' and create_time between ?2 and ?3  group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(String installSiteName, Date startTime, Date endTime, Pageable pageable);

    @Query(value = "select oldsite_id, oldsite_name, count(imei) as total_count, sum(if(isp='CT', 1,0)) as ct_count, "
            + "sum(if(isp='CMCC', 1,0)) as cmcc_count, sum(if(isp='CUCC', 1,0)) as cucc_count, create_time from t_device_dispatch_history where type='backoff' and old_site_name like '%?1%' and create_time between ?2 and ?3 and operator_id in (?4) group by day(create_time)", nativeQuery = true)
    public List<Map<String, Object>> backOffStatistic(String installSiteName, Date startTime, Date endTime, List<Integer> operatorIds, Pageable pageable);

}
