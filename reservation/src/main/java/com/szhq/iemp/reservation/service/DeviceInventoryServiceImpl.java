package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.reservation.api.model.TdeviceInventory;
import com.szhq.iemp.reservation.api.service.DeviceInventoryService;
import com.szhq.iemp.reservation.api.service.OperatorService;
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
    @Autowired
    private OperatorService operatorService;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Override
    public void updateDevStat(String imei, Integer devStat) {
        TdeviceInventory deviceInventory = findByImei(imei);
        if (deviceInventory != null) {
            deviceInventory.setDevstate(devStat);
            save(deviceInventory);
            redisUtil.del(CommonConstant.DEVICE_IMEI + imei);
            logger.info("update deviceInventory state success, imei is:" + imei);
        } else {
            logger.error("deviceInventory is not exist,imei is:" + imei);
            throw new NbiotException(CommonConstant.WRONG_CODE, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public TdeviceInventory findByImei(String imei) {
        String deviceInventoryString = (String) redisUtil.get(CommonConstant.DEVICE_IMEI + imei);
        if (StringUtils.isEmpty(deviceInventoryString)) {
            TdeviceInventory deviceInventory = deviceInventoryRepository.findByImei(imei);
            if (deviceInventory != null) {
                redisUtil.set(CommonConstant.DEVICE_IMEI + imei, JSON.toJSONString(deviceInventory), 5, TimeUnit.DAYS);
            }
            return deviceInventory;
        }
        logger.info("get device data from redis. imei:" + imei);
        TdeviceInventory deviceInventory = JSONObject.parseObject(deviceInventoryString, TdeviceInventory.class);
        return deviceInventory;
    }

    @Override
    public TdeviceInventory findByImeiAndInstallSiteIdIsNotNull(String imei) {
        TdeviceInventory deviceInventory = deviceInventoryRepository.findByImeiAndInstallSiteIdIsNotNull(imei);
        return deviceInventory;
    }

    @Override
    public TdeviceInventory save(TdeviceInventory entity) {
        TdeviceInventory device = null;
        try {
            device = deviceInventoryRepository.save(entity);
            logger.info("save device success, imei is:" + entity.getImei());
            deleteDeviceRedisKey();
            deleteRedisByImeiAndIotDeviceId(device.getImei(), device.getIotDeviceId());
            return device;
        } catch (Exception e) {
            logger.error("e:" + device.getImei(), e);
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
        TdeviceInventory deviceInventory = JSONObject.parseObject(deviceInventoryString, TdeviceInventory.class);
        return deviceInventory;
    }

    @Override
    public Integer deleteByImei(String imei) {
        return deviceInventoryRepository.deleteByImei(imei);
    }

    private void deleteRedisByImeiAndIotDeviceId(String imei, String iotDeviceId) {
        if(StringUtils.isNotEmpty(iotDeviceId)){
            String iotdeviceKey = CommonConstant.IOTDEVICEID + iotDeviceId;
            String key1 = CommonConstant.REGISTER_IOTDEVICEID + iotDeviceId;
            if (redisUtil.hasKey(iotdeviceKey)) {
                redisUtil.del(iotdeviceKey);
                logger.info("redis delete key [" + iotdeviceKey + "] success");
            }
            if (redisUtil.hasKey(key1)) {
                redisUtil.del(key1);
                logger.info("redis delete key [" + key1 + "] success");
            }
        }
        String deviceKey = CommonConstant.DEVICE_IMEI + imei;
        if (redisUtil.hasKey(deviceKey)) {
            redisUtil.del(deviceKey);
            logger.info("redis delete key [" + deviceKey + "] success");
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
