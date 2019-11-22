package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TuserPush;
import com.szhq.iemp.reservation.api.service.UserPushService;
import com.szhq.iemp.reservation.repository.UserPushRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
@Transactional
public class UserPushServiceImpl implements UserPushService {

    @Resource
    private UserPushRepository userPushRepository;


    @Override
    public TuserPush add(TuserPush userPush) {
        return userPushRepository.save(userPush);
    }

    @Override
    public Integer delete(String id) {
        return userPushRepository.deleteByUId(id);
    }

    @Override
    public Integer deleteByUserId(String userId) {
        return userPushRepository.deleteByUserId(userId);
    }
}
