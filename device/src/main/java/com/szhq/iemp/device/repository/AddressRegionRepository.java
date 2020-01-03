package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TaddressRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface AddressRegionRepository extends JpaRepository<TaddressRegion,Integer>, JpaSpecificationExecutor<TaddressRegion> {

	
	@Query(value="select * from t_adress_region where id=?1", nativeQuery = true)
	public TaddressRegion findByRegionId(Integer id);

	@Query(value="select * from t_adress_region where id in (?1)", nativeQuery = true)
	public List<TaddressRegion> findByRegionIds(List<Integer> ids);

	@Query(value="select * from t_adress_region where area_name like '%?1%' ", nativeQuery = true)
	public List<TaddressRegion> findByNameLike(String name);

	@Query(value=" select TAR1.id,TAR1.area_name,TAR1.area_code,TAR2.id as parent_id, TAR2.area_name as parent_area_name,"
				+ "TAR2.area_code as parent_area_code from t_adress_region TAR1 join t_adress_region TAR2 on TAR1.parent_id = TAR2.id "
				+ "where TAR2.id=?1", nativeQuery = true)
	public List<Map<String, Object>> findNexLevelAllChildrenById(Integer id);


	@Query(value="select city_id from t_install_site group by city_id", nativeQuery = true)
	public List<Integer> findAllSiteRegionIds();

	@Query(value="select region_id from t_install_site where city_id = ?1 group by region_id", nativeQuery = true)
    public List<Integer> getAllRegionsByCityId(Integer cityId);


}
