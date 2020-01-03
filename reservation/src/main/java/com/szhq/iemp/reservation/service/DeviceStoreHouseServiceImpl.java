package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TdeviceStoreHouse;
import com.szhq.iemp.reservation.api.service.DeviceStoreHouseService;
import com.szhq.iemp.reservation.repository.DeviceStoreHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class DeviceStoreHouseServiceImpl implements DeviceStoreHouseService {

    @Resource
    private DeviceStoreHouseRepository storeHouseRepository;


    @Override
    public TdeviceStoreHouse findById(Integer id) {
        return storeHouseRepository.findByStoreId(id);
    }


}
