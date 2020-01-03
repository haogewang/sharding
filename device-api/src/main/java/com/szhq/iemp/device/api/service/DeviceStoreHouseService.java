package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceStoreHouse;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.StorehouseQuery;

import java.util.List;

public interface DeviceStoreHouseService {
    /**
     * 仓库列表
     */
    MyPage<TdeviceStoreHouse> findAllByCriteria(Integer page, Integer size, String sorts, String orders, StorehouseQuery myQuery);

    /**
     * 添加仓库
     */
    TdeviceStoreHouse add(TdeviceStoreHouse entity);
    /**
     * 编辑
     */
    TdeviceStoreHouse update(TdeviceStoreHouse entity);
    /**
     * 新增（只新增仓库表）
     */
    TdeviceStoreHouse save(TdeviceStoreHouse entity);

    /**
     * 通过名字找设备仓库
     */
    TdeviceStoreHouse findByName(String name);

    /**
     * 通过id找设备仓库
     */
    TdeviceStoreHouse findById(Integer id);

    List<TdeviceStoreHouse> findByIds(List<Integer> storehouseIds);

    /**
     * 根据父Id查找下一级子类
     */
    List<TdeviceStoreHouse> findByParentId(Integer pId);

    /**
     * 根据运营公司查找仓库Id
     */
    List<Integer> findStorIdsByOperatorId(Integer operatorId);

    /**
     * 删除
     */
    Integer deleteById(Integer id);

    /**
     * 删除
     */
    Integer deleteByIds(List<Integer> ids);

    /**
     * 根据运营公司Ids删除仓库
     */
    Integer deleteByOperatorIds(List<Integer> operatorIds);

    /**
     *激活设备数量统计
     */
    List<ActiveDeviceCount> deviceActiveStatistic(Integer id, StorehouseQuery query);

    /**
     * 根据运营公司Id查找运营公司下所有仓库
     */
    List<TdeviceStoreHouse> findAllStoresByOperatorId(Integer operatorId);


}
