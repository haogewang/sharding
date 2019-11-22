package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TnoTrackerElec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoTrackerElecRepository extends JpaRepository<TnoTrackerElec, Long>, JpaSpecificationExecutor<TnoTrackerElec> {

    @Modifying
    @Query(value = "delete from t_no_tracker_elec where id = ?1", nativeQuery = true)
    public void deleteById(Long id);

    public TnoTrackerElec findByPlateNumber(String plateNo);

    @Query(value = "select NT.* from t_no_tracker_elec NT left join t_electrmobile_user EU on NT.id = EU.no_tracker_elec_id where NT.plate_number=?1 and EU.operator_id in (?2)", nativeQuery = true)
    public TnoTrackerElec findByPlateNumberAndOperatorIdsIn(String plateNo, List<Integer> operatorIdList);

    @Query(value = "select * from t_no_tracker_elec where id in (?1)", nativeQuery = true)
    public List<TnoTrackerElec> findByIds(List<Long> ids);

    @Query(value = "select * from t_no_tracker_elec where vin =?1 limit 1", nativeQuery = true)
    public TnoTrackerElec findByVin(String vin);

    @Query(value = "select TNE.* from t_no_tracker_elec TNE left join user U on TNE.user_id = U.id " +
            "inner join t_electrmobile_user TEU on TEU.no_tracker_elec_id = TNE.id where TEU.operator_id in (?4) " +
            "and if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?3 is null or ?3='', 1=1, U.name=?3) and if(?2 is null or ?2='', 1=1, TNE.plate_number=?2)",
            countQuery = "select count(TNE.id) from t_no_tracker_elec TNE left join user U on TNE.user_id = U.id " +
                    "inner join t_electrmobile_user TEU on TEU.no_tracker_elec_id = TNE.id where TEU.operator_id in (?4) and if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?3 is null or ?3='', 1=1, U.name=?3)" +
                    " and if(?2 is null or ?2='', 1=1, TNE.plate_number=?2)", nativeQuery = true)
    public Page<TnoTrackerElec> findByCretia(String phone, String plateNo, String username, List<Integer> operatorIds, Pageable pageable);

    @Query(value = "select TNE.* from t_no_tracker_elec TNE left join user U on TNE.user_id = U.id " +
            "inner join t_electrmobile_user TEU on TEU.no_tracker_elec_id = TNE.id where " +
            "if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?3 is null or ?3='', 1=1, U.name=?3) and if(?2 is null or ?2='', 1=1, TNE.plate_number=?2)",
            countQuery = "select count(TNE.id) from t_no_tracker_elec TNE left join user U on TNE.user_id = U.id " +
                    "inner join t_electrmobile_user TEU on TEU.no_tracker_elec_id = TNE.id where if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?3 is null or ?3='', 1=1, U.name=?3)" +
                    " and if(?2 is null or ?2='', 1=1, TNE.plate_number=?2)", nativeQuery = true)
    public Page<TnoTrackerElec> findByCretiaNoOperatorIds(String phone, String plateNo, String username, Pageable pageable);

}
