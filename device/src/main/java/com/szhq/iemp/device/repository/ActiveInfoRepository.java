package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TactiveInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
public interface ActiveInfoRepository extends JpaRepository<TactiveInfo,Long>,JpaSpecificationExecutor<TactiveInfo> {


    @Query(value = "select count(imei) from t_activator where date(create_time) = curdate() and activator_id = ?1 and mode = ?2", nativeQuery = true)
    public Integer countTodayActiveByActivatorId(String activatorId, Integer mode);

    @Query(value = "select * from t_activator where imei in (?1)", nativeQuery = true)
    List<TactiveInfo> findByImeis(List<String> imeis);

    @Query(value = "select count(imei) from t_activator where operator_id in (?1) and mode = ?2", nativeQuery = true)
    Integer countActiveByOperatorIds(List<Integer> operatorIds, int mode);

    @Query(value = "select count(imei) from t_activator where operator_id in (?1) and mode = ?2 and create_time between ?3 and ?4", nativeQuery = true)
    Integer countActiveByOperatorIds(List<Integer> operatorIds, int mode, Date startTime, Date endTime);

    @Query(value = "select count(imei) from t_activator where group_id in (?1) and mode = ?2", nativeQuery = true)
    Integer countActiveByGroupIds(List<String> groupIds, int mode);

    @Query(value = "select count(imei) from t_activator where group_id in (?1) and mode = ?2 and create_time between ?3 and ?4", nativeQuery = true)
    Integer countActiveByGroupIds(List<String> groupIds, int mode, Date startTime, Date endTime);

    @Query(value = "select DATE_FORMAT(`create_time`, '%Y-%m-%d') as days, count(distinct imei, if(mode=1,true,null)) AS active_count, count(distinct imei,if(mode=0,true,null)) as unactive_count from t_activator where operator_id in(?1) and datediff(now(),create_time) <= ?2 GROUP BY days ORDER BY create_time DESC", nativeQuery = true)
    List<Map<String, Object>> activeStatisticByOperatorId(List<Integer> ids, Integer offset);

    @Query(value = "select DATE_FORMAT(`create_time`, '%Y-%m-%d') as days, count(distinct imei, if(mode=1,true,null)) AS active_count, count(distinct imei,if(mode=0,true,null)) as unactive_count from t_activator where group_id =?1 and datediff(now(),create_time) <= ?2 GROUP BY days ORDER BY create_time DESC", nativeQuery = true)
    List<Map<String, Object>> activeStatisticByGroupId(String groupId, Integer offset);

    @Query(value = "select * from t_activator where group_id =?1", nativeQuery = true)
    List<TactiveInfo> getInfoByGroupId(String id);

    @Query(value = "select activator_id, activator_name, count(distinct imei, if(mode=1,true,null)) as active_count, count(distinct imei,if(mode=0,true,null)) as unactive_count from t_activator where group_id =?1 group by activator_id", nativeQuery = true)
    List<Map<String, Object>> getStatisticInfoByGroupId(String id);

    @Query(value = "select imei from t_activator where operator_id in (?1)", nativeQuery = true)
    List<String> findImeisByOperatorIds(List<Integer> operatorIds);


}
