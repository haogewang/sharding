package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.vo.*;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.InstallSiteQuery;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/10/18
 */
public interface DeviceInventoryService {
    /**
     * 列表
     */
    MyPage<TdeviceInventory> findAllByCriteria(Integer page, Integer size, String sorts,
                                               String orders, DeviceQuery query, Boolean isDispacher, Boolean isOutStore);

    /**
     * 查找安装点下所有设备
     */
    List<TdeviceInventory> findAllByInstallSiteId(Integer siteId);
    /**
     *根据imei查找设备
     */
    TdeviceInventory findByImei(String imei);
    /**
     * 根据imeis查找设备
     */
    List<TdeviceInventory> findByImeiIn(List<String> imeiList);
    /**
     * 根据箱号查找运营公司
     */
    List<Integer> getOperatorIdsByBoxNumbers(List<String> boxNumbers);
    /**
     * 根据批号查找运营公司
     */
    List<Integer> getOperatorIdsByDeliverSns(List<String> deliverSns);
    /**
     * 根据imeis查找运营公司
     */
    List<Integer> getOperatorIdsByImeis(List<String> imeis);

    Integer getDeviceNumberByOperatorId(Integer id);
    /**
     * 导入设备
     */
    Integer importDevice(List<TdeviceInventory> deviceList);
    /**
     * 保存
     */
    TdeviceInventory save(TdeviceInventory entity);
    /**
     *批量保存
     */
    List<TdeviceInventory> saveAll(List<TdeviceInventory> deviceList);
    /**
     * 根据imei删除
     */
    Integer deleteByImei(String imei);
    /**
     *根据箱号入库
     */
    Integer putinStorageByBoxNumbers(List<String> boxNumbers, Integer storehouseId, HttpServletRequest request);
    /**
     * 根据发货批号入库
     */
    Integer putInStorageByDeliverSns(List<String> deliverSns, Integer storehouseId, HttpServletRequest request);
    /**
     *根据imeis分配设备
     */
    Integer dispatchByImeis(List<String> imeis, Integer installSiteId);
    /**
     * 根据箱号分配设备
     */
    Integer dispatchByBoxNumber(List<String> boxNumbers, Integer installSiteId);
    /**
     *通过入库时间查找所有箱子及其设备数
     */
    Map<String, List<DeviceOfBox>> getBoxNumberByPutStorageTime(DeviceQuery query);
    /**
     *获取可退库的箱号
     */
    Map<String, List<DeviceOfBox>> getBackOffBoxNumbers(DeviceQuery query);
    /**
     *验证箱子是否符合入库要求
     */
    DeviceOfBox validPutStorageByBoxNumber(String boxNumber);
    /**
     *验证出库imei状态
     */
    TdeviceInventory validImeiInfo(String imei);
    /**
     *验证箱子是否符合退库要求
     */
    List<String> validBackoffInfoByBoxNumbers(List<String> boxNumbers);
    /**
     *验证设备是否符合退库要求
     */
    TdeviceInventory validBackoffInfoByImei(String imei);
    /**
     *验证设备是否符合退货要求
     */
    TdeviceInventory validReturnOffInfoByImei(String imei);
    /**
     * 根据箱号退库
     */
    Integer backOffByBoxNumbers(List<String> boxNumbers);
    /**
     * 根据设备号退库
     */
    Integer backOffByImeis(List<String> imeis);
    /**
     * 按箱子退库到华强
     */
    Integer backToHQByBoxNumber(List<String> boxNumbers);
    /**
     * 按设备列表退库到华强
     */
    Integer backToHQByImeis(List<String> imeis);
    /**
     * 根据imei退货
     */
    Integer returnOffDeviceByImeis(List<String> imeis);
    /**
     * 不良品转良品
     */
    Integer defectiveToNormalByImeis(List<String> imeis);
    /**
     * 入库统计
     */
    List<PutStorageCount> putStorageStatistic(Integer offset, Integer limit, DeviceQuery query);
    /**
     * 分配统计
     */
    List<DispachCount> dispatchStatistic(Integer offset, Integer limit, DeviceQuery query);
    /**
     * 退库统计
     */
    List<DispachCount> backOffStatistic(Integer offset, Integer limit, DeviceQuery query);
    /**
     *统计各运营商未分配设备数量
     */
    List<UnDispacheDeviceCount> getUndispacheDeviceCountOfIsp(DeviceQuery query);
    /**
     * 根据条件统计数量
     */
    Long countByCriteria(DeviceQuery deviceQuery);
    /**
     *根据运营公司ids统计设备数
     */
    Long countByOperatorIds(List<Integer> operatorIds);
    /**
     * 统计安装点下设备数
     */
    Integer getDeviceNumByInstallSiteId(Integer id);
    /**
     * 根据组id统计设备数
     */
    Integer countByGroupId(Integer groupId);
    /**
     * 根据组ids统计设备数
     */
    Integer countByGroupIds(List<Integer> groupIds);
    /**
     * 根据仓库Id计算该仓库设备数量
     */
    Long countByStoreHouseId(Integer id);
    /**
     * 根据仓库Ids计算该仓库设备数量
     */
    Long countByStoreHouseIds(List<Integer> subStoreIds);
    /**
     * 运营公司统计库存（未激活）量
     */
    Integer countUnActiveByOperatorIds(List<Integer> operatorIds);
    /**
     * 历史安装异常数
     */
    List<DispachCount> countHistoryInstalledUnormalCount(DeviceQuery query);
    /**
     *历史设备安装统计
     */
    List<DeviceCount> countHistoryInstalledByOffset(Integer offset, DeviceQuery query);
    /**
     * 根据imei修改激活状态
     */
    Integer updateActiveStateByImei(String imei, Boolean status);
    /**
     * 根据imei修改激活状态
     */
    Integer updateActiveStateByImeis(List<String> imeis, Boolean status);
    /**
     * 删除redis缓存
     */
    Integer deleteDeviceRedisData();
    /**
     *查找所有设备及运营公司关系
     */
    Map<String, Integer> findOperatorIdAndImeiRelations();
    /**
     * 获取可分配箱号
     */
    Map<String, List<DeviceOfBox>> getDispatchBoxNumbers(DeviceQuery query);
    /**
     * 根据仓库id修改仓库名
     */
    Integer updateStoreNameByStorehouseId(String storeName, Integer id);
    /**
     * 根据安装点id修改安装点名称
     */
    Integer updateSiteNameBySiteId(String name, Integer installSiteId);
    /**
     *移除设备分组
     */
    Integer removeGroupByImeis(List<String> imeis);
    /**
     * 统计库存数量（只统计310仓库已激活状态）
     */
    Integer countStoreCountByOperatorIds(List<Integer> operatorIds);
    /**
     * 统计所有未销售(库存)数量(包括310及302)
     */
    Integer countAllStoreCountByOperatorIds(List<Integer> operatorIds);

    /**
     *统计已安装设备数
     */
    Integer countInstalledCountByOperatorIds(List<Integer> operatorIds);
    /**
     * 统计库销售数量（仓库已激活状态）
     */
    Integer countSellCountByOperatorIds(List<Integer> allChildIds);
    /**
     * 获取仓库下未销售的设备
     */
    List<String> getUnSellDevicesByStorehouseId(Integer id);
    /**
     * 入库列表（按箱号展示）
     */
    Page<TdeviceInventory> getBoxNumbersOfPutStorage(Integer page, Integer size, DeviceQuery query);
    /**
     * 根据组Id查找设备
     */
    List<String> getImeisByGroupId(Integer groupId);
    /**
     * 根据箱号查找所有设备
     */
    List<TdeviceInventory> getDevicesByBoxNumber(String boxNumber);
    /**
     * 根据imei获取安装人员姓名
     */
    RegisterVo getInstalledWorkerByImei(String imei);
    /**
     * 根据imeis获取安装人员姓名
     */
    Map<String, RegisterVo> getInstalledWorkerByImeis(List<String> imeis);
    /**
     * 获取可入库的批号
     */
    List<TdeviceInventory> getDeliverSns(List<Integer> operatorIds);

    /**
     *根据imeis获取设备详情
     */
    List<TdeviceInventory> getInfoByImeis(List<String> imeis);
    /**
     * 找到所有已安装设备
     */
    List<TdeviceInventory> getAllInstalledDevices();


}
