package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Telectrmobile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ElectrombileRepository extends JpaRepository<Telectrmobile, Long>, JpaSpecificationExecutor<Telectrmobile> {


    @Query(value = "select * from t_electrmobile where electrmobile_id =?1", nativeQuery = true)
    Telectrmobile findByElecId(Long id);

    Telectrmobile findByPlateNumber(String plateNumber);

    List<Telectrmobile> findByVin(String vin);

    List<Telectrmobile> findByMotorNumber(String motorNumber);

    @Query(value = "select * from t_electrmobile where electrmobile_id in (?1)", nativeQuery = true)
    List<Telectrmobile> findAllElecsByElecIdIn(List<Long> ids);

    @Query(value = "select * from t_electrmobile where electrmobile_id in (?1) and (model_no=?2 or model_no is null)", nativeQuery = true)
    public List<Telectrmobile> findAllElectrombilesByElecIdInAndType(List<Long> ids, String type);

    @Query(value = "select * from t_electrmobile where imei in (?1) and (model_no=?2 or model_no is null)", nativeQuery = true)
    List<Telectrmobile> findAllElectrombilesByImeisInAndType(List<String> imeis, String type);

    @Query(value = "select e.* from t_electrmobile e where e.plate_number=?1 and e.operator_id in (?2)", nativeQuery = true)
    Telectrmobile findByPlateNumberAndOperatorIdsIn(String plateNumber, List<Integer> operatorIds);

    @Query(value = "select * from t_electrmobile where imei=?1", nativeQuery = true)
    Telectrmobile findByImei(String imei);

    @Query(value = "select * from t_electrmobile e where e.imei in (?1)", nativeQuery = true)
    List<Telectrmobile> findByImeiIn(List<String> imeis);

    @Query(value = "select imei, plate_number from t_electrmobile where imei in (?1)", nativeQuery = true)
    List<Map<String, Object>> getPlateNoByImeis(List<String> imeis);

    Integer countByPlateNumber(String plateNumber);

    @Modifying
    @Query(value = "delete from t_electrmobile where imei=?1", nativeQuery = true)
    Integer deleteByImei(String imei);

    @Modifying
    @Query(value = "delete from t_electrmobile where electrmobile_id=?1", nativeQuery = true)
    Integer deleteByElecId(Long id);

    @Modifying
    @Query(value = "update t_electrmobile set view_date=?1 where electrmobile_id=?2", nativeQuery = true)
    Integer updateViewDate(Date viewDate, Long electrombileId);

    @Modifying
    @Query(value = "update t_electrmobile set view_date=?2 where imei in (?1)", nativeQuery = true)
    Integer updateViewDateByImeis(List<String> imeis, Date date);

    @Modifying
    @Query(value = "update t_electrmobile set policy_no=?1 where imei=?2", nativeQuery = true)
    Integer updatePolicyNoByImei(String policyNo, String imei);


    @Query(value = "select TEU.*, TE.plate_number,TE.imei, TNTE.plate_number as notrackerPlateNo, U.name, U.phone, U.id_number from t_electrmobile_user TEU left join t_electrmobile TE on TEU.electrombile_id=TE.electrombile_id left join t_no_tracker_elec TNTE on TNTE.id=TEU.no_tracker_elec_id left join user U on U.id=TEU.user_id " +
            "where if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?2 is null or ?2='', 1=1, U.name=?2) and if(?3 is null or ?3='', 1=1, TE.plate_number=?3 or TNTE.plate_number=?3) " +
            "and if(?4 is null or ?4='', 1=1, TE.imei=?4) and if(?5 is null and ?6 is null, 1=1, TEU.create_time between ?5 and ?6)", nativeQuery = true)
    List<Map<String, Object>> findAllElecByCriteria(String phone, String realname, String plateNumber, String imei, Date startTime, Date endTime, Pageable pageable);

    @Query(value = "select TEU.*, TE.plate_number,TE.imei, TNTE.plate_number as notrackerPlateNo, U.name, U.phone, U.id_number from t_electrmobile_user TEU left join t_electrmobile TE on TEU.electrombile_id=TE.electrombile_id left join t_no_tracker_elec TNTE on TNTE.id=TEU.no_tracker_elec_id left join user U on U.id=TEU.user_id " +
            "where if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?2 is null or ?2='', 1=1, U.name=?2) and if(?3 is null or ?3='', 1=1, TE.plate_number=?3 or TNTE.plate_number=?3) " +
            "and if(?4 is null or ?4='', 1=1, TE.imei=?4) and if(?5 is null and ?6 is null, 1=1, TEU.create_time between ?5 and ?6) and TEU.operator_id in (?7)", nativeQuery = true)
    List<Map<String, Object>> findAllElecByCriteriaAndOperatorIds(String phone, String realname, String plateNumber, String imei, Date startTime, Date endTime, List<Integer> operatorIds, Pageable pageable);


    @Query(value = "select count(TEU.id) from t_electrmobile_user TEU left join t_electrmobile TE on TEU.electrombile_id=TE.electrombile_id left join t_no_tracker_elec TNTE on TNTE.id=TEU.no_tracker_elec_id left join user U on U.id=TEU.user_id " +
            "where if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?2 is null or ?2='', 1=1, U.name=?2) and if(?3 is null or ?3='', 1=1, TE.plate_number=?3 or TNTE.plate_number=?3) " +
            "and if(?4 is null or ?4='', 1=1, TE.imei=?4) and if(?5 is null and ?6 is null, 1=1, TEU.create_time between ?5 and ?6)", nativeQuery = true)
    Long countAllElecByCriteria(String phone, String realname, String plateNumber, String imei, Date startTime, Date endTime);

    @Query(value = "select count(TEU.id) from t_electrmobile_user TEU left join t_electrmobile TE on TEU.electrombile_id=TE.electrombile_id left join t_no_tracker_elec TNTE on TNTE.id=TEU.no_tracker_elec_id left join user U on U.id=TEU.user_id " +
            "where if(?1 is null or ?1='', 1=1, U.phone=?1) and if(?2 is null or ?2='', 1=1, U.name=?2) and if(?3 is null or ?3='', 1=1, TE.plate_number=?3 or TNTE.plate_number=?3) " +
            "and if(?4 is null or ?4='', 1=1, TE.imei=?4) and if(?5 is null and ?6 is null, 1=1, TEU.create_time between ?5 and ?6) and TEU.operator_id in (?7)", nativeQuery = true)
    Long countAllElecByCriteriaAndOperatorIds(String phone, String realname, String plateNumber, String imei, Date startTime, Date endTime, List<Integer> operatorIds);



}
