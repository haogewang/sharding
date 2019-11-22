package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TelectrombileColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectrombileColorRepository extends JpaRepository<TelectrombileColor, Integer>, JpaSpecificationExecutor<TelectrombileColor> {

    @Modifying
    @Query(value = "delete from t_electrmobile_color where color_id = ?1", nativeQuery = true)
    public Integer deleteElecColorById(Integer id);

}
