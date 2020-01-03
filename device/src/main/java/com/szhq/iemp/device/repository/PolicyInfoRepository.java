package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TpolicyInfo;
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
public interface PolicyInfoRepository extends JpaRepository<TpolicyInfo,Long>,JpaSpecificationExecutor<TpolicyInfo> {

    @Query(value = "select * from t_policy_info where user_id = :userId", nativeQuery = true)
    public List<TpolicyInfo> getPolicyInfoByUserId(@Param("userId") String userId);

    @Query(value = "select * from t_policy_info where imei = ?1", nativeQuery = true)
    public TpolicyInfo findByImei(String imei);

    @Query(value = "select * from t_policy_info where imei=?1 and user_id = ?2", nativeQuery = true)
    TpolicyInfo findByImeiAndUserId(String imei, String userId);

    @Query(value = "select * from t_policy_info where plate_no = ?1", nativeQuery = true)
    public TpolicyInfo findByPlateNo(String plateNo);

    @Query(value = "select TPI.* from t_policy_info TPI where TPI.active_time between ?1 and ?2 and TPI.operator_id in (?3)", nativeQuery = true)
    public List<TpolicyInfo> findAllByDate(Date startTime, Date endTime, List<Integer> operatorIdList);

    //    @Query(value = "select TPI.*, TI.type as iType from t_insurance TI " +
//                    "inner join t_user_insurance TUI on TI.id=TUI.insurance_id " +
//                    "left join t_policy_info TPI on TUI.policy_id = TPI.id " +
//                    "where TPI.active_time between ?1 and ?2", nativeQuery = true)
    @Query(value = "select TPI.* from t_policy_info TPI where TPI.active_time between ?1 and ?2", nativeQuery = true)
    public List<TpolicyInfo> findAllByDate(Date startTime, Date endTime);

    @Modifying
    @Query(value = "delete from t_policy_info where imei in (?1) and user_id is null", nativeQuery = true)
    Integer deleteNoInstalledPolicyByImeis(List<String> imeis);

    @Query(value = "select * from t_policy_info where id = ?1", nativeQuery = true)
    TpolicyInfo getPolicyById(long id);

    @Query(value = "select * from t_policy_info where imei in (?1)", nativeQuery = true)
    List<TpolicyInfo> findByImeis(List<String> imeis);

    @Modifying
    @Query(value = "delete from t_policy_info where id in (?1)", nativeQuery = true)
    void deleteByIds(List<Long> ids);

    //临时
    @Query(value = "select r.imei,r.operator_id,r.user_id from t_registration r left join t_device_storehouse DS on DS.operator_id=r.operator_id  where to_days(r.create_time) =to_days(now()) and r.imei is not null and DS.policy_name_code is not null",nativeQuery = true)
    List<Map<String, Object>> getLostPolicys();

    @Query(value = "select TDI.imei,TDI.isp,TDI.operator_name, TDI.storehouse_name, TPI.name_code, TPI.name, if(TPI.name_code is null or TPI.name_code ='','false','true') as ishaspolicy from t_device_inventory TDI left join t_policy_info TPI on TDI.imei=TPI.imei where TDI.model_no='W310' and TDI.operator_id in (?1) and if(?2 is null or ?2='', 1=1, TDI.imei = ?2)", nativeQuery = true)
    List<Map<String, Object>> all310devices(List<Integer> operatorIdList, String imei, Pageable pageable);

    @Query(value = "select TDI.imei,TDI.isp,TDI.operator_name, TDI.storehouse_name, TPI.name_code, TPI.name, if(TPI.name_code is null or TPI.name_code ='','false','true') as ishaspolicy from t_device_inventory TDI left join t_policy_info TPI on TDI.imei=TPI.imei where TDI.model_no='W310' and if(?1 is null or ?1='', 1=1, TDI.imei = ?1)", nativeQuery = true)
    List<Map<String, Object>> all310devices(String imei, Pageable pageable);

    @Query(value = "select count(1) from t_device_inventory where operator_id in (?1) and model_no='W310' and if(?2 is null or ?2='', 1=1, imei = ?2)", nativeQuery = true)
    Integer countAll310devices(List<Integer> operatorIdList, String imei);

    @Query(value = "select count(1) from t_device_inventory where model_no='W310' and if(?1 is null or ?1='', 1=1, imei = ?1)", nativeQuery = true)
    Integer countAll310devices(String imei);

    @Query(value = "select TDI.imei,TDI.isp,TDI.operator_name, TDI.storehouse_name, TPI.name_code, TPI.name, if(TPI.name_code is null or TPI.name_code ='','false','true') as ishaspolicy from t_device_inventory TDI left join t_policy_info TPI on TDI.imei=TPI.imei where TDI.model_no='W310'", nativeQuery = true)
    List<Map<String, Object>> all310devices(Pageable pageable);

    @Query(value = "select count(1) from t_device_inventory where model_no='W310'", nativeQuery = true)
    Integer countAll310devices();
}
