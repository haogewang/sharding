package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TelectrombileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectrombileUserRepository extends JpaRepository<TelectrombileUser,Integer>,JpaSpecificationExecutor<TelectrombileUser>{

	List<TelectrombileUser> findByUserId(String userId);

	List<TelectrombileUser> findByElectrombileId(Long elecId);

	Integer deleteByElectrombileId(Long id);

	@Modifying
	@Query(value = "delete from t_electrmobile_user where user_id=?1", nativeQuery = true)
	Integer deleteByUserId(String userId);

	@Query(value = "select * from t_electrmobile_user where user_id = ?1 and operator_id in (?2)", nativeQuery = true)
	List<TelectrombileUser> findByUserIdAndOperatorIds(String userId, List<Integer> operatorIds);

	@Query(value = "select * from t_electrmobile_user where user_id = ?1 and electrombile_id =?2", nativeQuery = true)
	TelectrombileUser findByUserIdAndElecId(String userId, Long elecId);
}
