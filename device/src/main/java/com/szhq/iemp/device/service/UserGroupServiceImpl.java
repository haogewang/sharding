package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.TuserGroup;
import com.szhq.iemp.device.api.service.UserGroupService;
import com.szhq.iemp.device.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    @Resource
    private UserGroupRepository userGroupRepository;


    @Override
    public TuserGroup findById(String id) {
        return null;
    }

    @Override
    public List<TuserGroup> findByOperatorIdIn(List<Integer> operatorIds) {
        return userGroupRepository.findByOperatorIdIn(operatorIds);
    }
}
