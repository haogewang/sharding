package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TpolicyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PolicyInfoRepository extends JpaRepository<TpolicyInfo,Long>,JpaSpecificationExecutor<TpolicyInfo> {

    @Query(value = "select * from t_policy_info where imei = ?1 and user_id = ?2", nativeQuery = true)
    public TpolicyInfo findByImeiAndUserId(String imei, String userId);

    @Query(value = "select * from t_policy_info where imei = ?1 and user_id is null", nativeQuery = true)
    TpolicyInfo findByImeiAndUserIdisNull(String imei);

    @Query(value = "select * from t_policy_info where imei = ?1", nativeQuery = true)
    TpolicyInfo findByImei(String imei);
}
