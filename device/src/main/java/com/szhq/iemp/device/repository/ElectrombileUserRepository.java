package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TelectrombileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectrombileUserRepository extends JpaRepository<TelectrombileUser, Integer>, JpaSpecificationExecutor<TelectrombileUser> {

    public List<TelectrombileUser> findByUserId(String userId);

    @Query(value = "select no_tracker_elec_id from t_electrombile_user where user_id = ?1", nativeQuery = true)
    public List<Integer> findNoTrackerElecIdByUserId(String userId);

    public List<TelectrombileUser> findByElectrombileId(Integer elecId);

    @Query(value = "select * from t_electrombile_user where electrombile_id in (?1)", nativeQuery = true)
    public List<TelectrombileUser> findByElecIdIn(List<Integer> ids);

    public TelectrombileUser findByNoTrackerElecId(Integer elecId);

    public Integer deleteByUserId(String userId);

    public Integer deleteByElectrombileId(Integer id);

    @Modifying
    @Query(value = "delete from t_electrombile_user where no_tracker_elec_id = ?1", nativeQuery = true)
    public Integer deleteByNoTrackerElecId(Integer id);

}
