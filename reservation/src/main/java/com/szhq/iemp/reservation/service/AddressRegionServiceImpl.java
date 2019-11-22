package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.reservation.api.model.TaddressRegion;
import com.szhq.iemp.reservation.api.service.AddressRegionService;
import com.szhq.iemp.reservation.repository.AddressRegionRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class AddressRegionServiceImpl implements AddressRegionService {


    @Resource
    private AddressRegionRepository regionRepository;
    @Autowired
    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Override
    public TaddressRegion findById(Integer id) {
        TaddressRegion region = (TaddressRegion)redisUtil.get(CommonConstant.REGION_ID + id);
        if(region == null){
            region = regionRepository.findByRegionId(id);
            if(region != null){
                redisUtil.set(CommonConstant.REGION_ID + id, region);
            }
            return region;
        }
        log.info("get region data from redis.id:" + id);
        return region;
    }


}
