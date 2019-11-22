package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TinstallSite;
import com.szhq.iemp.device.api.model.Toperator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OperatorRepository extends JpaRepository<Toperator, Integer>, JpaSpecificationExecutor<Toperator> {

    @Modifying
    @Query(value = "delete from t_operator where id = ?1", nativeQuery = true)
    public Integer deleteByOperatId(Integer id);

    @Modifying
    @Query(value = "delete from t_operator where id in (?1)", nativeQuery = true)
    public Integer deleteByOperatIds(List<Integer> ids);

    public Toperator findByName(String name);

    @Query(value = "select storehouse_id from t_operator where id in (?1)", nativeQuery = true)
    public List<Integer> findStorehouseIdByOperatorIds(List<Integer> ids);

    @Query(value = "select TO1.id,TO1.name, TO2.id as parentId, TO1.storehouse_id from t_operator TO1 join t_operator TO2 on TO1.parent_id=TO2.id where TO2.id=?1", nativeQuery = true)
    public List<Map<String, Object>> findAllChildrenById(Integer id);

    @Query(value = "select id from t_operator where storehouse_id  = ?1", nativeQuery = true)
    public Integer findOperatorIdByStoreHouseId(Integer storehouseId);

    @Query(value = "select * from t_operator where parent_id  = ?1", nativeQuery = true)
    public List<Toperator> findNextOperator(Integer parentId);

    @Query(value = "select * from t_operator where parent_id is null", nativeQuery = true)
    List<Toperator> getAllFirstOperator();

    @Query(value = "select * from t_operator where (parent_id=?1 or id= ?1)", nativeQuery = true)
    List<Toperator> getChildrenByPId(Integer id);

    @Query(value = "select distinct (operator_id) from t_device_inventory where storehouse_id  = 1", nativeQuery = true)
    List<Integer> findNeedPutStorage();

    @Query(value = "select * from t_operator where id in (?1)", nativeQuery = true)
    List<Toperator> findByIds(List<Integer> operatorIds);

    @Query(value = "select O.* from t_operator O left join t_device_storehouse TDS on O.storehouse_id=TDS.id where O.parent_id=?1 and TDS.is_active = ?2", nativeQuery = true)
    public List<Toperator> getChildrenByPIdAndType(Integer id, Boolean isStoreActive);

    @Query(value = "select O.* from t_operator O left join t_device_storehouse TDS on O.storehouse_id=TDS.id where O.parent_id is null and TDS.is_active = ?1", nativeQuery = true)
    public List<Toperator> getAllFirstTrueOperator(Boolean isStoreActive);
}
