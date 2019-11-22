package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Tregistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface RegistrationRepository extends JpaRepository<Tregistration,Long>,JpaSpecificationExecutor<Tregistration> {


	public Tregistration findByRegisterId(Long registrationId);

	@Query(value="select * from t_registration r where r.imei=?1", nativeQuery = true)
	public Tregistration findByImei(String imei);

	Tregistration findByPhone(String phone);

	@Modifying
	@Query(value="delete from t_registration where register_id=?1", nativeQuery = true)
	public Integer deleteByRegisterId(Long id);

	@Modifying
	@Query(value="delete from t_registration where imei=?1", nativeQuery = true)
	public int deleteByImei(String imei);

	@Query(value="select count(1) from t_registration r where r.user_id=?1", nativeQuery = true)
	public Integer countByUserId(String userId);


	@Query(value="select TR.register_id, TR.create_time, TE.plate_number, TE.policy_no, TR.imei, U.name as username, U.phone, U.id_number, U.birthplace, U.contact_phone," +
			" TET.name as type, TEV.name as vendor, TEC.name as color, TE.vin, TE.purchase_time, TIS.name as install_site_name, TIS.person_in_charge, TIS.phone as install_site_phone,TPS.name as police_station_name," +
			" TDI.devstate, TDI.isp" +
			" from t_registration TR left join t_electrmobile TE " +
			" on TR.electrmobile_id=TE.electrmobile_id " +
			" left join user U on U.id=TR.user_id " +
			" left join t_device_inventory TDI on TDI.imei=TR.imei " +
			" left join t_install_site TIS on TIS.install_site_id=TDI.install_site_id " +
			" left join t_electrmobile_color TEC on TEC.color_id=TE.color_id " +
			" left join t_electrmobile_type TET on TET.type_id=TE.type_id " +
			" left join t_electrmobile_vendor TEV on TEV.vendor_id=TE.vendor_id " +
			" left join t_police_station TPS on TPS.id=TIS.police_id where TR.create_time between ?1 and ?2", nativeQuery = true)
	public List<Map<String, Object>> exportAllByTime(Date start, Date end);
	
	@Query(value="select TR.register_id, TR.create_time, TE.plate_number, TE.policy_no, TR.imei, U.name as username, U.phone, U.id_number, U.birthplace, U.contact_phone," +
			"TET.name as type, TEV.name as vendor, TEC.name as color, TE.vin, TE.purchase_time, TIS.name as install_site_name, TIS.person_in_charge, TIS.phone as install_site_phone,TPS.name as police_station_name, TDI.devstate, TDI.isp" +
			" from t_registration TR left join t_electrmobile TE " +
			"on TR.electrmobile_id=TE.electrmobile_id " +
			"left join user U on U.id=TR.user_id " +
			"left join t_device_inventory TDI on TDI.imei=TR.imei " +
			"left join t_install_site TIS on TIS.install_site_id=TDI.install_site_id " +
			"left join t_electrmobile_color TEC on TEC.color_id=TE.color_id " +
			"left join t_electrmobile_type TET on TET.type_id=TE.type_id " +
			"left join t_electrmobile_vendor TEV on TEV.vendor_id=TE.vendor_id " +
			"left join t_police_station TPS on TPS.id=TIS.police_id where TR.create_time between ?1 and ?2 and TDI.operator_id in (?3)", nativeQuery = true)
	public List<Map<String, Object>> exportAllByTime(Date start, Date end, List<Integer> operatorIds);



}
