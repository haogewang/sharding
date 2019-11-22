package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TdeviceState;

import java.util.List;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/10/16
 */
public interface DeviceStateService {

    Map<String, TdeviceState> getStateByImeis(List<String> imeis);
}
