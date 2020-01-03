package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.reservation.api.model.TdeviceInventory;
import com.szhq.iemp.reservation.api.service.DeviceInventoryService;
import com.szhq.iemp.reservation.repository.DeviceInventoryRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class DeviceInventoryServiceImpl implements DeviceInventoryService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceInventoryServiceImpl.class);

    @Resource
    private DeviceInventoryRepository deviceInventoryRepository;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Override
    public void updateDevStat(String imei, Integer devStat) {
        TdeviceInventory device = findByImei(imei);
        if (device != null) {
            device.setDevstate(devStat);
            save(device);
            logger.info("update device state success, imei is:{}",imei);
        } else {
            logger.error("device is not exist.imei is:{}",imei);
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public TdeviceInventory findByImei(String imei) {
//        TdeviceInventory deviceInventory = deviceInventoryRepository.findByImei(imei);
//        return deviceInventory;
        String deviceString = (String) redisUtil.get(CommonConstant.DEVICE_IMEI + imei);
        if (deviceString == null) {
            TdeviceInventory device = deviceInventoryRepository.findByImei(imei);
            if (device != null) {
                redisUtil.set(CommonConstant.DEVICE_IMEI + imei, JSONObject.toJSONString(device), 3600*24);
            }
            return device;
        }
        logger.info("get device data from redis . imei:" + imei);
        TdeviceInventory device = JSONObject.parseObject(deviceString, TdeviceInventory.class);
        return device;
    }

    @Override
    public TdeviceInventory findByImeiAndInstallSiteIdIsNotNull(String imei) {
        return deviceInventoryRepository.findByImeiAndInstallSiteIdIsNotNull(imei);
    }

    @Override
    public TdeviceInventory save(TdeviceInventory entity) {
        TdeviceInventory device = null;
        try {
            device = deviceInventoryRepository.save(entity);
            logger.info("save device success, imei is:" + entity.getImei());
            deleteDeviceRedisKey();
            deleteRedisByIotDeviceId(device.getIotDeviceId());
            return device;
        } catch (Exception e) {
            logger.error("e:" + entity.getImei(), e);
            throw new NbiotException(CommonConstant.WRONG_CODE, e.getMessage());
        }
    }

    @Override
    public TdeviceInventory findByIotDeviceId(String iotDeviceId) {
        String deviceInventoryString = (String) redisUtil.get(CommonConstant.IOTDEVICEID + iotDeviceId);
        if (StringUtils.isEmpty(deviceInventoryString)) {
            TdeviceInventory deviceInventory = deviceInventoryRepository.findByIotDeviceId(iotDeviceId);
            if(deviceInventory != null){
                redisUtil.set(CommonConstant.IOTDEVICEID + iotDeviceId, JSON.toJSONString(deviceInventory), 10, TimeUnit.DAYS);
            }
            return deviceInventory;
        }
        return JSONObject.parseObject(deviceInventoryString, TdeviceInventory.class);
    }

    @Override
    public Integer deleteByImei(String imei) {
        return deviceInventoryRepository.deleteByImei(imei);
    }

    private void deleteRedisByIotDeviceId(String iotDeviceId) {
        if(StringUtils.isNotEmpty(iotDeviceId)){
            String iotDeviceKey = CommonConstant.IOTDEVICEID + iotDeviceId;
            String key1 = CommonConstant.REGISTER_IOTDEVICEID + iotDeviceId;
            if (redisUtil != null && redisUtil.hasKey(iotDeviceKey)) {
                redisUtil.del(iotDeviceKey);
                logger.info("redis delete key [" + iotDeviceKey + "] success");
            }
            if (redisUtil != null && redisUtil.hasKey(key1)) {
                redisUtil.del(key1);
                logger.info("redis delete key [" + key1 + "] success");
            }
        }
    }

    @Autowired
    public void deleteDeviceRedisKey() {
        if (redisUtil.keys(CommonConstant.DEVICE_PATTERN) != null && redisUtil.keys(CommonConstant.DEVICE_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.DEVICE_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
            }
        }
        if (redisUtil.keys(CommonConstant.SITE_PATTERN) != null && redisUtil.keys(CommonConstant.SITE_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.SITE_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
            }
        }
        if(redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN) != null && redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN);
            for(String key : sets) {
                redisUtil.del(key);
            }
        }
    }

}
