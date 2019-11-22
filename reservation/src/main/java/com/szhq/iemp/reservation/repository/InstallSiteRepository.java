package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TinstallSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface InstallSiteRepository extends JpaRepository<TinstallSite,Integer>,JpaSpecificationExecutor<TinstallSite> {

	public TinstallSite findByInstallSiteId(Integer id);

}
