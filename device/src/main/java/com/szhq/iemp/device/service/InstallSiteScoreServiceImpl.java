package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.TinstallSiteScore;
import com.szhq.iemp.device.api.service.InstallSiteScoreService;
import com.szhq.iemp.device.api.service.InstallSiteService;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;
import com.szhq.iemp.device.repository.InstallSiteScoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/9/2
 */
@Service
@Transactional
@Slf4j
public class InstallSiteScoreServiceImpl implements InstallSiteScoreService {

    @Resource
    private InstallSiteScoreRepository installSiteScoreRepository;
    @Autowired
    private InstallSiteService installSiteService;

    @Override
    public Integer save(TinstallSiteScore score) {
        TinstallSiteScore tinstallSiteScore = installSiteScoreRepository.save(score);
        installSiteService.deleteInstallSiteRedisData();
        return tinstallSiteScore.getId();
    }

    @Override
    public InstallSiteAndWorker getInstallSiteAndWorkerInfo(String imei) {
        List<Map<String, Object>> lists = installSiteScoreRepository.getInstallSiteAndWorkerInfo(imei);
        InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
        if (lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                String userId = (String) map.get("id");
                String userName = (String) map.get("name");
                Integer installSiteId = (Integer) map.get("install_site_id");
                String installSiteName = (String) map.get("installSiteName");
                installSiteAndWorker.setInstallSiteId(installSiteId);
                installSiteAndWorker.setInstallSiteName(installSiteName);
                installSiteAndWorker.setWorkerId(userId);
                installSiteAndWorker.setWorkerName(userName);
            }
        }
        return installSiteAndWorker;
    }

    @Override
    public Boolean isScore(String imei) {
       TinstallSiteScore installSiteScore = installSiteScoreRepository.findByImei(imei);
       if(installSiteScore != null){
           return true;
       }
        return false;
    }

    @Override
    public List<InstallSiteAndWorker> getAllInstallSiteAvgScore() {
        List<Map<String, Object>> lists = installSiteScoreRepository.getInstallSiteAvgScore();
        List<InstallSiteAndWorker> result = new ArrayList<>();
        if (lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
                setValue(installSiteAndWorker, map);
                result.add(installSiteAndWorker);
            }
        }
        return result;
    }

    @Override
    public Double getInstallSiteAvgScoreByInstallId(Integer id) {
        List<Map<String, Object>> lists = installSiteScoreRepository.getInstallSiteAvgScoreById(id);
        InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
        if (lists != null && !lists.isEmpty()){
            Map<String, Object> map = lists.get(0);
            setValue(installSiteAndWorker, map);
        }
        return installSiteAndWorker.getAvgScore();
    }

    @Override
    public Double getWorkerAvgScoreByWorkerId(String userId) {
        List<Map<String, Object>> lists = installSiteScoreRepository.getInstallWorkerAvgScore(userId);
        InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
        if (lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                Double avgScore = (Double) map.get("avgScore");
                Integer installSiteId = (Integer) map.get("install_site_id");
                String installSiteName = (String) map.get("name");
                installSiteAndWorker.setAvgScore(avgScore);
                installSiteAndWorker.setInstallSiteId(installSiteId);
                installSiteAndWorker.setInstallSiteName(installSiteName);
            }
        }
        return installSiteAndWorker.getAvgScore();
    }

    @Override
    public List<TinstallSiteScore> getDetailByInstallSiteId(Integer installSiteId) {
        List<TinstallSiteScore> lists = installSiteScoreRepository.findByInstallSiteId(installSiteId);
        return lists;
    }

    @Override
    public List<TinstallSiteScore> getDetailByWorkerId(String workerId) {
//        return installSiteScoreRepository.findByWorkerId(workerId);
        List<Map<String, Object>> lists = installSiteScoreRepository.findByWorkerId(workerId);
        List<TinstallSiteScore> result = new ArrayList<>();
        if (lists != null && !lists.isEmpty()) {
            for (Map<String, Object> map : lists) {
                TinstallSiteScore installSiteScore = new TinstallSiteScore();
                Integer id = (Integer) map.get("id");
                String createBy = (String) map.get("userName");
                Date createTime = (Date) map.get("create_time");
                String comment = (String) map.get("comment");
                String imei = (String) map.get("imei");
                Integer siteId = (Integer) map.get("install_site_id");
                Integer siteScore = (Integer) map.get("install_site_score");
                Integer workerScore = (Integer) map.get("install_worker_score");
                String workerName = (String) map.get("workerName");
                String workerid = (String) map.get("workerId");
                installSiteScore.setId(id);
                installSiteScore.setComment(comment);
                installSiteScore.setImei(imei);
                installSiteScore.setInstallSiteId(siteId);
                installSiteScore.setInstallSiteScore(siteScore);
                installSiteScore.setInstallWorkerScore(workerScore);
                installSiteScore.setUserName(createBy);
                installSiteScore.setCreateTime(createTime);
                result.add(installSiteScore);
            }
        }
        return result;
    }

    @Override
    public List<InstallSiteAndWorker> getWorkerAvgScoreAndInfoByInstallSiteId(Integer installSiteId) {
        List<Map<String, Object>> lists = installSiteScoreRepository.getWorkerAvgScoreAndInfoByInstallSiteId(installSiteId);
        List<InstallSiteAndWorker> result = new ArrayList<>();
        if (lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
                Object avgScoreString = (Object) map.get("avgWorkerScore");
                Double avgScore = Double.valueOf(String.valueOf(avgScoreString));
                DecimalFormat df = new DecimalFormat("#.0");
                String temp = df.format(avgScore);
                avgScore = Double.valueOf(temp);
                String workerId = (String) map.get("id");
                String workerName = (String) map.get("name");
                installSiteAndWorker.setAvgScore(avgScore);
                installSiteAndWorker.setWorkerId(workerId);
                installSiteAndWorker.setWorkerName(workerName);
                result.add(installSiteAndWorker);
            }
        }
        return result;
    }

    @Override
    public Integer deleteBySiteId(Integer siteId) {
        Integer count = installSiteScoreRepository.deleteBySiteId(siteId);
        return count;
    }

    private void setValue(InstallSiteAndWorker installSiteAndWorker, Map<String, Object> map) {
        Double avgScore = (Double) map.get("avg_score");
        Integer installSiteId = (Integer) map.get("install_site_id");
        String installSiteName = (String) map.get("install_site_name");
        installSiteAndWorker.setAvgScore(avgScore);
        installSiteAndWorker.setInstallSiteId(installSiteId);
        installSiteAndWorker.setInstallSiteName(installSiteName);
    }
}
