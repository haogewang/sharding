package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TplatenoPrefix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNoPrefixRepository extends JpaRepository<TplatenoPrefix,Integer>,JpaSpecificationExecutor<TplatenoPrefix>{


	@Modifying
	@Query(value = "delete from t_platenoprefix where id = ?1", nativeQuery = true)
	Integer deleteByPrefixId(Integer id);
}
