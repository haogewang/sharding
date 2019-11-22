package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.device.api.model.TelectrombileUser;
import com.szhq.iemp.device.api.service.ElecmobileUserService;
import com.szhq.iemp.device.repository.ElectrombileUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ElecmobileUserServiceImpl implements ElecmobileUserService {

    @Resource
    private ElectrombileUserRepository electrombileUserRepository;

    @Override
    public List<TelectrombileUser> findByUserId(String userId) {
        return electrombileUserRepository.findByUserId(userId);
    }

    @Override
    public List<Integer> findNoTrackerElecIdByUserId(String userId) {
        return electrombileUserRepository.findNoTrackerElecIdByUserId(userId);
    }

    @Override
    public List<TelectrombileUser> findByElecId(Integer elecId) {
        return electrombileUserRepository.findByElectrombileId(elecId);
    }

    @Override
    public TelectrombileUser findByNoTrackerElecId(Integer noTrackerElecId) {
        return electrombileUserRepository.findByNoTrackerElecId(noTrackerElecId);
    }

    @Override
    public TelectrombileUser save(TelectrombileUser entity) {
        return electrombileUserRepository.save(entity);
    }

    @Override
    public Integer deleteByUserId(String userId) {
        return electrombileUserRepository.deleteByUserId(userId);
    }

    @Override
    public Integer deleteByElecId(Integer id) {
        return electrombileUserRepository.deleteByElectrombileId(id);
    }

    @Override
    public Integer deleteByNoTrackerElecId(Integer noTrackerElecId) {
        return electrombileUserRepository.deleteByNoTrackerElecId(noTrackerElecId);
    }

}
