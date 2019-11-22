package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TcommonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TcommonConfigRepository extends JpaRepository<TcommonConfig, Integer>, JpaSpecificationExecutor<TcommonConfig> {

    public TcommonConfig findByName(String name);
}
