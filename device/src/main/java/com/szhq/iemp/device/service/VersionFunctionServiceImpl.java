package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.TversionFunction;
import com.szhq.iemp.device.api.service.VersionFunctionService;
import com.szhq.iemp.device.repository.VersionFunctionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
@Transactional
public class VersionFunctionServiceImpl implements VersionFunctionService {

    @Resource
    private VersionFunctionRepository versionFunctionRepository;


    @Override
    public TversionFunction findByFwVersion(String fwVersion) {
        return versionFunctionRepository.findByFwVersion(fwVersion);
    }
}
