package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TsaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/11/19
 */
@Repository
public interface SaleRecordRepository extends JpaRepository<TsaleRecord,Long>, JpaSpecificationExecutor<TsaleRecord> {


    @Query(value = "select * from t_sale_record where imei = ?1 and mode = ?2", nativeQuery = true)
    TsaleRecord findByImeiAndMode(String imei, Integer mode);

    @Query(value = "select DATE_FORMAT(`create_time`, '%Y-%m-%d') as days,count(distinct imei, if(mode=(1 or 2),true,false)) AS active_count, count(distinct imei,if(mode=0,true,null)) as unactive_count from t_sale_record where operator_id in(?1) and datediff(now(),create_time) <= ?2 GROUP BY days ORDER BY create_time DESC", nativeQuery = true)
    List<Map<String, Object>> saleStatisticByOperatorId(List<Integer> ids, Integer offset);

    @Query(value = "select DATE_FORMAT(`create_time`, '%Y-%m-%d') as days,count(distinct imei, if(mode=(1 or 2),true,false)) AS active_count, count(distinct imei,if(mode=0,true,null)) as unactive_count from t_sale_record where group_id =?1 and datediff(now(),create_time) <= ?2 GROUP BY days ORDER BY create_time DESC", nativeQuery = true)
    List<Map<String, Object>> saleStatisticByGroupId(String groupId, Integer offset);

    @Query(value = "select count(distinct imei) from t_sale_record where operator_id in (?1) and mode = ?2", nativeQuery = true)
    Integer countSaleByOperatorIdsAndType(List<Integer> operatorIds, Integer type);

    @Query(value = "select count(distinct imei) from t_sale_record where operator_id in (?1) and if(?2=0, mode =0, mode <> 0)", nativeQuery = true)
    Integer countSaleByOperatorIds(List<Integer> operatorIds, Integer type);

    @Query(value = "select count(distinct imei) from t_sale_record where operator_id in (?1) and if(?2=0, mode =0, mode <> 0) and create_time between ?3 and ?4", nativeQuery = true)
    Integer countSaleByOperatorIds(List<Integer> operatorIds, Integer type, Date startTime, Date endTime);

    @Query(value = "select count(distinct imei) from t_sale_record where group_id in (?1) and mode = ?2", nativeQuery = true)
    Integer countSaleByGroupIds(List<String> groupIds, int mode);

    @Query(value = "select count(distinct imei) from t_sale_record where group_id in (?1) and mode = ?2 and create_time between ?3 and ?4", nativeQuery = true)
    Integer countSaleByGroupIds(List<String> groupIds, int mode, Date startTime, Date endTime);

    @Query(value = "select imei from t_sale_record where operator_id in (?1)", nativeQuery = true)
    List<String> findImeisByOperatorIds(List<Integer> operatorIds);
}
