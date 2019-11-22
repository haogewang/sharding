package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceDefectiveInventory;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;

import java.util.List;

public interface DeviceDefectiveInventoryService {
    /**
     * 列表
     */
     MyPage<TdeviceDefectiveInventory> findByCretira(Integer offset, Integer limit, String sort, String order, DeviceQuery query);
    /**
     * 添加
     */
     TdeviceDefectiveInventory save(TdeviceDefectiveInventory entity);
    /**
     * 批量添加
     */
     List<TdeviceDefectiveInventory> saveAll(List<TdeviceDefectiveInventory> list);
    /**
     * 根据imei删除
     */
     Integer deleteByImei(String imei);
    /**
     * 通过imei查找
     */
     TdeviceDefectiveInventory findByImei(String imei);


}
