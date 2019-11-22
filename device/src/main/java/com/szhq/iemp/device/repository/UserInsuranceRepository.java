package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TuserInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserInsuranceRepository extends JpaRepository<TuserInsurance,Integer>,JpaSpecificationExecutor<TuserInsurance> {


    @Query(value = "select * from t_user_insurance where policy_id =?1",nativeQuery = true)
    List<TuserInsurance> findByPolicyId(Long policyId);
}
