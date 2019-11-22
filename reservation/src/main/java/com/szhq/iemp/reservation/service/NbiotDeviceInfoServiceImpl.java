package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.NbiotDeviceInfo;
import com.szhq.iemp.reservation.api.service.NbiotDeviceInfoService;
import com.szhq.iemp.reservation.repository.NbiotDeviceInfoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author wanghao
 * @date 2019/8/8
 */
@Service
@Transactional
public class NbiotDeviceInfoServiceImpl implements NbiotDeviceInfoService {

    @Resource
    private NbiotDeviceInfoRepository nbiotDeviceInfoRepository;

    @Override
    public NbiotDeviceInfo add(NbiotDeviceInfo entity) {
        return nbiotDeviceInfoRepository.save(entity);
    }

    @Override
    public int deleteByImei(String oldImei) {
        int i = nbiotDeviceInfoRepository.deleteByImei(oldImei);
        return i;
    }

    @Override
    public int updateOperatorByImei(String imei, Integer id) {
        int i = nbiotDeviceInfoRepository.updateOperatorByImei(imei, id);
        return i;
    }
}
