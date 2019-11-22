package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TelectrombileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectrombileTypeRepository extends JpaRepository<TelectrombileType,Integer>,JpaSpecificationExecutor<TelectrombileType>{
	
	@Modifying
	@Query(value = "delete from t_electrmobile_type where type_id = ?1", nativeQuery = true)
	public Integer deleteByTypeId(Integer id);
}
