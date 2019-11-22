package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.reservation.api.model.TcommonConfig;
import com.szhq.iemp.reservation.api.service.CommonService;
import com.szhq.iemp.reservation.repository.TcommonConfigRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private TcommonConfigRepository commonConfigRepository;

    @Override
    public TcommonConfig findByName(String name) {
        return commonConfigRepository.findByName(name);
    }

    @Override
    public Integer setByName(String name, String value) {
        TcommonConfig commonConfig = findByName(name);
        if (commonConfig == null) {
            throw new NbiotException(404, "can not find this key!");
        }
        commonConfig.setValue(value);
        commonConfigRepository.save(commonConfig);
        return 1;
    }

}
