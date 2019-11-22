package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TiotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface IotTypeRepository extends JpaRepository<TiotType,Integer>, JpaSpecificationExecutor<TiotType> {

	@Modifying
	@Query(value="delete from t_iot_type where id=?1", nativeQuery = true)
	public Integer deleteByIotTypeId(Integer id);

	public TiotType findByName(String name);	
}
