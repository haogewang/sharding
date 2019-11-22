package com.szhq.iemp.device.repository;

import com.szhq.iemp.device.api.model.TinstallSiteScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InstallSiteScoreRepository extends JpaRepository<TinstallSiteScore,Integer>,JpaSpecificationExecutor<TinstallSiteScore> {

	@Query(value="select * from t_install_site_score where imei=?1", nativeQuery = true)
	public TinstallSiteScore findByImei(String imei);

	@Query(value="select * from t_install_site_score where install_site_id=?1", nativeQuery = true)
	public List<TinstallSiteScore> findByInstallSiteId(Integer siteId);

	@Query(value="select u.id, u.name, i.install_site_id, i.name as installSiteName from t_registration r left join user u on r.create_by = u.id left join t_install_site i on u.install_site_id = i.install_site_id where r.imei = ?1", nativeQuery = true)
	public List<Map<String, Object>> getInstallSiteAndWorkerInfo(String imei);

	@Query(value="select install_site_id,install_site_name, AVG(install_site_score) as avg_score from t_install_site_score group by install_site_id order by avgScore desc", nativeQuery = true)
    public List<Map<String, Object>> getInstallSiteAvgScore();

	@Query(value="select install_site_id, install_site_name, AVG(install_site_score) as avg_score from t_install_site_score where install_site_id=?1 group by install_site_id", nativeQuery = true)
	public List<Map<String, Object>> getInstallSiteAvgScoreById(Integer installSiteId);

	@Query(value="select AVG(install_site_score) as avg_score from t_install_site_score where worker_id=?1 group by worker_id", nativeQuery = true)
	public List<Map<String, Object>> getInstallWorkerAvgScore(String workerId);

	@Query(value="select TISS.id, U1.name as userName, TISS.create_time, TISS.comment, TISS.imei, TISS.install_site_id, TISS.install_site_score, TISS.install_worker_score, U.name as workerName, U.id as workerId from t_install_site_score TISS left join user U on TISS.worker_id=U.id left join user U1 on TISS.create_by = U1.id where TISS.worker_id=?1", nativeQuery = true)
	public List<Map<String, Object>> findByWorkerId(String userId);

    @Query(value="select U.id, U.name, AVG(TISS.install_worker_score) as avgWorkerScore from t_install_site_score TISS left join user U on TISS.worker_id = U.id where TISS.install_site_id=?1 group by TISS.worker_id order by avgWorkerScore desc", nativeQuery = true)
    public List<Map<String, Object>> getWorkerAvgScoreAndInfoByInstallSiteId(Integer installSiteId);

    @Modifying
	@Query(value="delete from t_install_site_score where install_site_id=?1", nativeQuery = true)
    public Integer deleteBySiteId(Integer siteId);
}
