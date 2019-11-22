package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.reservation.api.model.TelectrombileUser;
import com.szhq.iemp.reservation.api.service.ElecmobileUserService;
import com.szhq.iemp.reservation.repository.ElectrombileUserRepository;
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
    public List<TelectrombileUser> findByUserIdAndOperatorIds(String userId, List<Integer> operatorIds) {
        List<TelectrombileUser> result = electrombileUserRepository.findByUserIdAndOperatorIds(userId, operatorIds);
        return result;
    }

    @Override
    public List<Long> findNoTrackerElecIdByUserId(String userId) {
        return electrombileUserRepository.findNoTrackerElecIdByUserId(userId);
    }

    @Override
    public List<TelectrombileUser> findByElecId(Long elecId) {
        return electrombileUserRepository.findByElectrombileId(elecId);
    }

    @Override
    public TelectrombileUser findByNoTrackerElecId(Long noTrackerElecId) {
        return electrombileUserRepository.findByNoTrackerElecId(noTrackerElecId);
    }

    @Override
    public TelectrombileUser save(TelectrombileUser entity) {
        return electrombileUserRepository.save(entity);
    }

    @Override
    public Long update(TelectrombileUser entity) {
        TelectrombileUser elecUser = electrombileUserRepository.save(entity);
        return elecUser.getId();
    }

    @Override
    public Integer deleteByUserId(String userId) {
        return electrombileUserRepository.deleteByUserId(userId);
    }

    @Override
    public Integer deleteByElecId(Long id) {
        return electrombileUserRepository.deleteByElectrombileId(id);
    }

    @Override
    public Integer deleteByNoTrackerElecId(Long noTrackerElecId) {
        return electrombileUserRepository.deleteByNoTrackerElecId(noTrackerElecId);
    }

    @Override
    public List<String> findByElecIdIn(List<Long> ids) {
        List<String> userIds = new ArrayList<>();
        List<TelectrombileUser> elecUsers = electrombileUserRepository.findByElecIdIn(ids);
        if (elecUsers != null && elecUsers.size() > 0) {
            userIds = elecUsers.stream().map(TelectrombileUser::getUserId).collect(Collectors.toList());
        }
        log.info("userIds:" + JSONObject.toJSONString(userIds));
        return userIds;
    }


}
