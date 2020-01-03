package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.Tuser;
import com.szhq.iemp.device.api.service.UserService;
import com.szhq.iemp.device.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public Tuser findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<Tuser> findBySiteId(Integer siteId) {
        return userRepository.findBySiteId(siteId);
    }

}
