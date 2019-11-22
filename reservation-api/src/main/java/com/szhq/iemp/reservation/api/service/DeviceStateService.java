package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TdeviceState;

/**
 * @author wanghao
 * @date 2019/10/16
 */
public interface DeviceStateService {

    Integer deleteByImei(String imei);

    TdeviceState save(TdeviceState entity);
}
