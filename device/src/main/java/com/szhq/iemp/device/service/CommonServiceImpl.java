package com.szhq.iemp.device.service;

import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.device.api.model.TcommonConfig;
import com.szhq.iemp.device.api.service.CommonService;
import com.szhq.iemp.device.repository.TcommonConfigRepository;
import com.szhq.iemp.device.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional
public class CommonServiceImpl implements CommonService {

    @Resource
    private TcommonConfigRepository commonConfigRepository;

    @Autowired
    private RedisUtil redisUtil;

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

    @Override
    public int deleteRedisByKey(String key) {
        int count = 0;
        if (redisUtil.hasKey(key)) {
            redisUtil.del(key);
            count ++;
            log.info("redis delete key [" + key + "] success");
        }
        return count;
    }

}
