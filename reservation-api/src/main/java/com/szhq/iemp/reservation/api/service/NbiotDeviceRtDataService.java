package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.NbiotDeviceRtData;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.vo.NbiotRtDataVo;

import java.util.List;

public interface NbiotDeviceRtDataService {

    NbiotDeviceRtData findByImei(String imei);

    NbiotDeviceRtData findByImei(String imei, List<Integer> operatorIds);

    List<NbiotDeviceRtData> findByImeiIn(List<Telectrmobile> elecs);

    List<NbiotDeviceRtData> findByImeis(List<String> imeis);

    List<NbiotRtDataVo> findRtDataByElecs(List<Telectrmobile> elecs);

    List<NbiotDeviceRtData> getData(String lowerLeft, String upperRight, List<Integer> operatorIds);

    NbiotDeviceRtData getLocation(String imei);

    Integer deleteByImei(String imei);

    Integer save(List<NbiotDeviceRtData> entities);


}
