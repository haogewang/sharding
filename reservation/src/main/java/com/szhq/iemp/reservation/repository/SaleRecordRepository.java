package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TsaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author wanghao
 * @date 2019/11/19
 */
@Repository
public interface SaleRecordRepository extends JpaRepository<TsaleRecord,Long>, JpaSpecificationExecutor<TsaleRecord> {


    @Query(value = "select * from t_sale_record where imei = ?1 and mode = ?2", nativeQuery = true)
    TsaleRecord findByImeiAndMode(String imei, Integer mode);
}
