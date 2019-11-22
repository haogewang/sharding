package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TversionFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionFunctionRepository extends JpaRepository<TversionFunction,Integer>,JpaSpecificationExecutor<TversionFunction> {

	@Query(value="select * from version_function where fw_version=?1", nativeQuery = true)
	public TversionFunction findByFwVersion(String fwVersion);

}
