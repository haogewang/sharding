package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceDispatchHistory;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;

import java.util.List;

public interface DeviceDispacheHistoryService {
    /**
     * 列表（模糊查询）
     */
    MyPage<TdeviceDispatchHistory> findAllByCriteria(Integer page, Integer size, String sort, String order, DeviceQuery query);

    /**
     * 添加
     */
    TdeviceDispatchHistory save(TdeviceDispatchHistory entity);

    /**
     * 批量添加
     */
    List<TdeviceDispatchHistory> saveAll(List<TdeviceDispatchHistory> list);

    /**
     * 删除
     */
    Integer deleteById(Integer id);

    /**
     * 通过imei查找
     */
    TdeviceDispatchHistory findByImei(String imei);

    /**
     * 删除redis缓存
     */
    Integer deleteDisPatchRedis();

}
