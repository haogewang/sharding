package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.Tinsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface InsuranceRepository extends JpaRepository<Tinsurance,Integer>,JpaSpecificationExecutor<Tinsurance> {

    @Modifying
    @Query(value = "delete from t_insurance where id =?1",nativeQuery = true)
    public Integer deleteByInsuranceId(Integer id);

    @Query(value = "select distinct (name_code), name from t_insurance",nativeQuery = true)
    public List<Map<String, Object>> getAllNames();

    @Query(value = "select * from t_insurance where id in (?1)",nativeQuery = true)
    public List<Tinsurance> findByIdIn(List<Integer> ids);
}
