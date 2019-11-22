package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Trole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TroleRepository extends JpaRepository<Trole, Integer>, JpaSpecificationExecutor<Trole> {

    @Query(value = "select r.id from role r where r.type=2", nativeQuery = true)
    public String getRoleId();
}
