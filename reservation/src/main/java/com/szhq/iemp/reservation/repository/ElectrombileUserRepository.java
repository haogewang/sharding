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

	public List<TelectrombileUser> findByUserId(String userId);
	
	@Query(value = "select no_tracker_elec_id from t_electrmobile_user where user_id = ?1", nativeQuery = true)
	public List<Long> findNoTrackerElecIdByUserId(String userId);
	
	public List<TelectrombileUser> findByElectrombileId(Long elecId);

	@Query(value = "select * from t_electrmobile_user where electrombile_id in (?1)", nativeQuery = true)
	public List<TelectrombileUser> findByElecIdIn(List<Long> ids);
	
	public TelectrombileUser findByNoTrackerElecId(Long elecId);

	public Integer deleteByElectrombileId(Long id);

	@Modifying
	@Query(value = "delete from t_electrmobile_user where user_id=?1", nativeQuery = true)
	public Integer deleteByUserId(String userId);

	@Modifying
	@Query(value = "delete from t_electrmobile_user where no_tracker_elec_id = ?1", nativeQuery = true)
	public Integer deleteByNoTrackerElecId(Long id);

	@Query(value = "select * from t_electrmobile_user where user_id = ?1 and operator_id in (?2)", nativeQuery = true)
    public List<TelectrombileUser> findByUserIdAndOperatorIds(String userId, List<Integer> operatorIds);
}
