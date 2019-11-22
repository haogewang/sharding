package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TelectrombileVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectrombileVendorRepository extends JpaRepository<TelectrombileVendor,Integer>,JpaSpecificationExecutor<TelectrombileVendor>{

	@Modifying
	@Query(value = "delete from t_electrmobile_vendor where vendor_id = ?1", nativeQuery = true)
	public Integer deleteElecVendorById(Integer id);

	@Query(value = "select * from t_electrmobile_vendor order by search_index asc", nativeQuery = true)
	List<TelectrombileVendor> findAll();
}
