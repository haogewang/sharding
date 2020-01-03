package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TinstallSite;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;
import com.szhq.iemp.device.api.vo.InstallSiteDeviceCount;
import com.szhq.iemp.device.api.vo.query.InstallSiteQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface InstallSiteService {

    /**
     * 查找符合条件的安装点
     */
    MyPage<TinstallSite> findAllByCriteria(Integer page, Integer size, String sort, String order, InstallSiteQuery query);

    /**
     *根据id查找安装点
     */
    TinstallSite findById(Integer installSiteId);
    /**
     *根据名称查找安装点
     */
    TinstallSite findByName(String name);
    /**
     * 添加安装点
     */
    TinstallSite add(TinstallSite entity);
    /**
     * 修改
     */
    Integer update(TinstallSite entity);
    /**
     * 删除
     */
    Integer deleteById(Integer id);
    /**
     * 修改最大预约数
     */
    void setMaxReservationNumBySiteId(Integer siteId, Integer value);

    /**
     * 修改最大预约天数
     */
    void setMaxReservationDaysBySiteId(Integer siteId, Integer value);

    /**
     * 根据安装点id查找该安装点设备总数
     */
    Integer getTotalDeviceBySiteId(Integer id);

    /**
     * 根据安装点id查找该安装点历史安装数
     */
    Integer getEquipDeviceBySiteId(Integer id);

    /**
     * 根据安装点id查找该安装点设备在线数
     */
    Integer getOnlineEquipDeviceBySiteId(Integer id);

    /**
     * 根据安装点id查找该安装点今日已安装数
     */
    Integer getTodayInstalledCountBySiteId(Integer id);

    /**
     * 根据安装点id查找该安装点今日在线数
     */
    Integer getTodayOnlineCountBySiteId(Integer id);
    /**
     * 根据安装点id查找该安装点分数
     */
    Double getScoreBySiteId(Integer id);
    /**
     *通过区域id获取安装点
     */
    List<TinstallSite> getSitesByRegionId(Integer id, Boolean status);

    /**
     * 通过regionId模糊查找安装点
     */
    List<TinstallSite> getSitesByRegionIdLike(String id);

    /**
     *通过cityId获取安装点
     */
    List<TinstallSite> getSitesByCityId(Integer id, Boolean status);
    /**
     * 删除redis数据
     */
    Integer deleteInstallSiteRedisData();

    /**
     * 根据时间偏移计算某个安装点安装数（过去一周、月）
     */
    List<InstallSiteDeviceCount> countByOffsetAndInstallSiteId(Integer installSiteId, Integer offset);
    /**
     * 上线率排名
     */
    List<InstallSiteDeviceCount> countByCondation(InstallSiteQuery query);
    /**
     * 安装点排名
     */
    List<InstallSiteDeviceCount> installSiteOrder(Integer offset, InstallSiteQuery query);

    /**
     * 统计数量
     */
    Long countByQuery(InstallSiteQuery query);

    List<InstallSiteAndWorker> getInstalledCountByUserId(String userId, Integer siteId, Integer offset);

    List<InstallSiteAndWorker> getInstalledWorkerCountBySiteId(Date startTime, Date endTime, Integer siteId);

    List<InstallSiteAndWorker> getInstalledWorkerCountBySiteId(Integer siteId);
    /**
     *安装点列表导出
     */
    void export(Integer siteId, HttpServletResponse response);
    /**
     * 安装统计导出
     */
    void exportWorkerInstalledInfo(InstallSiteQuery query, HttpServletResponse response);

    List<InstallSiteAndWorker> installedWorkerCountStatisticBySiteId(Date startTime, Date endTime, Integer installSiteId);


}
