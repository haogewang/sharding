package com.szhq.iemp.device.service;

import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceManufactor;
import com.szhq.iemp.device.api.service.DeviceManufactorService;
import com.szhq.iemp.device.repository.DeviceManufactorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Slf4j
@Service
@Transactional
public class DeviceManufactorServiceImpl implements DeviceManufactorService {

    @Resource
    private DeviceManufactorRepository deviceManufactorRepository;

    @Override
    public MyPage<TdeviceManufactor> findAllByCriteria(Integer page, Integer size, String sorts, String orders) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TdeviceManufactor> pages = deviceManufactorRepository.findAll(pageable);
        return new MyPage<TdeviceManufactor>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public TdeviceManufactor save(TdeviceManufactor entity) {
        return deviceManufactorRepository.save(entity);
    }

    @Override
    public Integer deleteById(Integer id) {
        Integer i = deviceManufactorRepository.deleteByManufactorId(id);
        log.info("delete manufactor id is:" + id);
        return i;
    }

    @Override
    public TdeviceManufactor findByName(String name) {
        return deviceManufactorRepository.findByName(name);
    }

    @Override
    public TdeviceManufactor findById(Integer id) {
        return deviceManufactorRepository.findById(id).orElse(null);
    }



}
