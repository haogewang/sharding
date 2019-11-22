package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.Tuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Tuser,String>,JpaSpecificationExecutor<Tuser> {

	@Query(value="select * from user where id_number=?1", nativeQuery = true)
	public Tuser findByIdNumber(String idNumber);
	
	@Query(value="select * from user where phone=?1", nativeQuery = true)
	public Tuser findByPhone(String phone);
	
	@Query(value="select * from user where name=?1", nativeQuery = true)
	public List<Tuser> findByOwnerName(String name);

	@Query(value="select * from user where id in (?1)", nativeQuery = true)
	public List<Tuser> findByIdIn(List<String> userIds);

}
