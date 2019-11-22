package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TinstallSiteScore;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;

import java.util.List;

/**
 * @author wanghao
 * @date 2019/9/2
 */
public interface InstallSiteScoreService {
    /**
     * 添加
     */
    Integer save(TinstallSiteScore score);

    /**
     * 获取安装点及安装人员信息
     */
    InstallSiteAndWorker getInstallSiteAndWorkerInfo(String imei);

    /**
     * 是否已经评价
     */
    Boolean isScore(String imei);

    /**
     * 获取所有安装点平均分
     * @return
     */
    List<InstallSiteAndWorker> getAllInstallSiteAvgScore();

    /**
     * 获取安装点平均分
     */
    Double getInstallSiteAvgScoreByInstallId(Integer installSiteId);
    /**
     * 获取安装人员平均分
     */
    Double getWorkerAvgScoreByWorkerId(String userId);

    /**
     * 根据安装点Id获取安装点评分详情
     */
    List<TinstallSiteScore> getDetailByInstallSiteId(Integer installSiteId);

    /**
     * 根据安装人员Id获取安装点评分详情
     */
    List<TinstallSiteScore> getDetailByWorkerId(String userId);
    /**
     * 根据安装点Id获取安装人员信息及平均分
     */
    List<InstallSiteAndWorker> getWorkerAvgScoreAndInfoByInstallSiteId(Integer installSiteId);

    /**
     * 根据安装点id删除
     */
    Integer deleteBySiteId(Integer siteId);
}
