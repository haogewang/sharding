package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Tgroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GroupRepository extends JpaRepository<Tgroup,Integer>, JpaSpecificationExecutor<Tgroup> {

	@Query(value="select id, name, operator_id, parent_id, custom_type from t_group where id =?1 and type = ?2", nativeQuery = true)
	List<Map<String, Object>> findByGroupIdAndType(Integer id, Integer type);

}
