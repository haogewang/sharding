package com.szhq.iemp.reservation.api.service;


import com.szhq.iemp.reservation.api.model.TdeviceInventory;

public interface DeviceInventoryService {

    /**
     * 通过imei修改设备状态信息及运营公司
     * @param imei
     * @param devStat (1:已安装、2:更换、0:未安装 )
     */
    void updateDevStat(String imei, Integer devStat);

    /**
     * 根据设备号删除
     */
     Integer deleteByImei(String imei);

    /**
     * 根据imei查找设备信息
     */
     TdeviceInventory findByImei(String imei);

    /**
     * 通过iemi查找未安装设备
     */
     TdeviceInventory findByImeiAndInstallSiteIdIsNotNull(String imei);

    /**
     * 保存设备信息
     */
     TdeviceInventory save(TdeviceInventory entity);

    /**
     * 通过iotDeviceId查设备
     */
     TdeviceInventory findByIotDeviceId(String iotDeviceId);

     void deleteDeviceRedisKey();

}
