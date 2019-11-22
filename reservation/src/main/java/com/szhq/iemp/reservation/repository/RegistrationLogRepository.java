package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TregistrationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationLogRepository extends JpaRepository<TregistrationLog, Integer>, JpaSpecificationExecutor<TregistrationLog> {


    List<TregistrationLog> findByRegisterId(Long registerId);
}
