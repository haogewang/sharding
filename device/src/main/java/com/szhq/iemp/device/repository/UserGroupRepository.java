package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TuserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<TuserGroup,String>,JpaSpecificationExecutor<TuserGroup> {

	@Query(value="select * from user_group where operator_id in (?1)", nativeQuery = true)
	List<TuserGroup> findByOperatorIdIn(List<Integer> operatorIds);
}
