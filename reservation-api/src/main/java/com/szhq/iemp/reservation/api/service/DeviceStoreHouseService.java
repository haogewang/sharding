package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TdeviceStoreHouse;

public interface DeviceStoreHouseService {

    /**
     * 通过id找设备仓库
     */
    TdeviceStoreHouse findById(Integer id);

}
