package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.TdeviceState;
import com.szhq.iemp.device.api.model.TversionFunction;
import com.szhq.iemp.device.api.service.DeviceStateService;
import com.szhq.iemp.device.api.service.VersionFunctionService;
import com.szhq.iemp.device.repository.DeviceStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/10/16
 */
@Service
@Transactional
@Slf4j
public class DeviceStateServiceImpl implements DeviceStateService {

    @Resource
    private DeviceStateRepository deviceStateRepository;
    @Autowired
    private VersionFunctionService versionFunctionService;

    @Override
    public Map<String, TdeviceState> getStateByImeis(List<String> imeis) {
        Map<String, TdeviceState> map = new HashMap<>();
        for (String imei : imeis) {
            TdeviceState tdeviceState = deviceStateRepository.findByImei(imei);
            if (tdeviceState != null) {
                TversionFunction versionFunction = versionFunctionService.findByFwVersion(tdeviceState.getFwVersion());
                if(versionFunction != null){
                    tdeviceState.setFunction(versionFunction.getFunction());
                }
                map.put(imei, tdeviceState);
            } else {
                map.put(imei, null);
            }
        }
        return map;
    }
}
