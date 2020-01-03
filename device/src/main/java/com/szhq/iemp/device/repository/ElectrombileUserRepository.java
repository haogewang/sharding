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

     List<TelectrombileUser> findByUserId(String userId);

}
