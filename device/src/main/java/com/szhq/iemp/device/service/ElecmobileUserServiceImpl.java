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
    public TelectrombileUser save(TelectrombileUser entity) {
        return electrombileUserRepository.save(entity);
    }

}
