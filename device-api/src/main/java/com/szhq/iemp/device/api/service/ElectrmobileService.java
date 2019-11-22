package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;

import java.util.List;

public interface ElectrmobileService {

    /**
     * 模糊查询
     */
    MyPage<Telectrmobile> findElecByCriteria(Integer offset, Integer limit, String sort, String order, ElecmobileQuery elecQuery);
    /**
     * 通过运营公司Id获取已安装设备数
     */
    Integer getDeviceNumberByOperatorId(Integer operatorId);
    /**
     * 根据条件统计数量
     */
    Long countByCriteria(DeviceQuery deviceQuery);
    /**
     * 查询设备是否安装
     */
    Integer countByImei(String imei);
    /**
     * 获取车辆类型
     */
    String getTypeById(Integer typeId);
    /**
     * 获取车辆品牌
     */
    String getVendorById(Integer vendorId);
    /**
     * 根据电动车Ids查找设备
     */
    List<String> findImeisByElecIds(List<Long> elecIds);
    /**
     *根据仓库id修改仓库名
     */
    Integer updateStoreNameByStorehouseId(String name, Integer id);
    /**
     * 删除电动车缓存
     */
    Integer deleteElecRedisData();
    /**
     * 根据安装点id修改安装点名称
     */
    Integer updateSiteNameBySiteId(String name, Integer installSiteId);
    /**
     * 根据imeis查找车辆
     */
    List<Telectrmobile> findByImeis(List<String> imeis);
    /**
     * 批量保存
     */
    Integer saveAll(List<Telectrmobile> electrmobiles);

    /**
     * 按组统计车辆数
     */
    Integer countByGroupId(Integer id);

    /**
     * 移除车辆分组
     */
    Integer removeGroupByImeis(List<String> imeis);
}
