package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.Tgroup;
import com.szhq.iemp.device.api.model.TiotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface GroupRepository extends JpaRepository<Tgroup,Integer>, JpaSpecificationExecutor<Tgroup> {

	@Modifying
	@Query(value="delete from t_group where id=?1", nativeQuery = true)
	Integer deleteByGroupId(Integer id);

	Tgroup findByName(String name);

	@Query(value="select username, plate_number, imei from t_registration where imei in (?1)", nativeQuery = true)
	List<Map<String, Object>> getUserAndElecInfoByImeis(List<String> imeis);

	@Query(value="select id, name, parent_id, operator_id from t_group where if(?1 is null or ?1=0, parent_id is null, parent_id =?1)", nativeQuery = true)
	List<Map<String, Object>>findNextGroups(Integer parentId);

	@Query(value="select id, name, parent_id, operator_id from t_group where if(?1 is null or ?1=0, parent_id is null, parent_id =?1) and type = ?2", nativeQuery = true)
	List<Map<String, Object>> findNextGroups(Integer parentId, Integer type);

	@Query(value="select id, name, operator_id, parent_id, type from t_group where id =?1", nativeQuery = true)
	List<Map<String, Object>> findByGroupId(Integer id);

}
