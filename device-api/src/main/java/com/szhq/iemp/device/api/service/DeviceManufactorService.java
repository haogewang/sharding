package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceManufactor;

public interface DeviceManufactorService {

    /**
     * 列表
     */
    MyPage<TdeviceManufactor> findAllByCriteria(Integer page, Integer size, String sorts, String orders);
    /**
     * 添加
     */
    TdeviceManufactor save(TdeviceManufactor entity);

    /**
     * 删除
     */
    Integer deleteById(Integer id);

    /**
     * 通过名称查找
     */
    TdeviceManufactor findByName(String name);

    /**
     * 通过id查找
     */
    TdeviceManufactor findById(Integer id);



}
