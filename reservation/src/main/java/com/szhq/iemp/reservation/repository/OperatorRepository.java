package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Toperator;
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

    @Query(value = "select TO1.id,TO1.name, TO2.id as parentId, TO1.storehouse_id from t_operator TO1 join t_operator TO2 on TO1.parent_id=TO2.id where TO2.id=?1", nativeQuery = true)
    public List<Map<String, Object>> findAllChildrenById(Integer id);
}
