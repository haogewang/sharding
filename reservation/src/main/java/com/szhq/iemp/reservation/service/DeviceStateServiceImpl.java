package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TdeviceState;
import com.szhq.iemp.reservation.api.service.DeviceStateService;
import com.szhq.iemp.reservation.repository.DeviceStateRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author wanghao
 * @date 2019/10/16
 */
@Service
@Transactional
public class DeviceStateServiceImpl implements DeviceStateService {

    @Resource
    private DeviceStateRepository deviceStateRepository;

    @Override
    public Integer deleteByImei(String imei) {
        return deviceStateRepository.deleteByImei(imei);
    }

    @Override
    public TdeviceState save(TdeviceState entity) {
        return deviceStateRepository.save(entity);
    }
}
