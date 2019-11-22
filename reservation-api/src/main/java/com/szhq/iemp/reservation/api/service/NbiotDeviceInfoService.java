package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.NbiotDeviceInfo;

/**
 * @author wanghao
 * @date 2019/8/8
 */
public interface NbiotDeviceInfoService {
    /**
     * 添加
     */
    NbiotDeviceInfo add(NbiotDeviceInfo entity);

    /**
     * 根据imei删除
     */
    int deleteByImei(String oldImei);


    int updateOperatorByImei(String imei, Integer id);
}
