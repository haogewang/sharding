package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TpolicyInfo;
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
}
