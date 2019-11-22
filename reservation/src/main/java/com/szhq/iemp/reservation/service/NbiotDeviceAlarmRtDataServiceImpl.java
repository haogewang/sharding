package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.NbiotDeviceRtAlarm;
import com.szhq.iemp.reservation.api.service.NbiotDeviceAlarmRtDataService;
import com.szhq.iemp.reservation.repository.TnbiotDeviceRtAlarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class NbiotDeviceAlarmRtDataServiceImpl implements NbiotDeviceAlarmRtDataService {

    @Resource
    private TnbiotDeviceRtAlarmRepository deviceRtAlarmRepository;

    @Override
    public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon) {
        return deviceRtAlarmRepository.getAlarmRealTimeDatas(lowerLeftLat, lowerLeftLon, upperRightLat, upperRightLon);
    }

    @Override
    public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, double lowerLeftLat, double lowerLeftLon, double upperRightLat, double upperRightLon) {
        return deviceRtAlarmRepository.getAlarmRealTimeDatas(type, lowerLeftLat, lowerLeftLon, upperRightLat, upperRightLon);
    }

    @Override
    public Integer deleteByImei(String imei) {
        return deviceRtAlarmRepository.deleteByImei(imei);
    }

    @Override
    public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(List<Integer> operatorIds, double lowerLeftLat,
                                                          double lowerLeftLon, double upperRightLat, double upperRightLon) {
        return deviceRtAlarmRepository.getAlarmRealTimeDatas(operatorIds, lowerLeftLat, lowerLeftLon, upperRightLat, upperRightLon);
    }

    @Override
    public List<NbiotDeviceRtAlarm> getAlarmRealTimeDatas(String type, List<Integer> operatorIds, double lowerLeftLat,
                                                          double lowerLeftLon, double upperRightLat, double upperRightLon) {
        return deviceRtAlarmRepository.getAlarmRealTimeDatas(type, operatorIds, lowerLeftLat, lowerLeftLon, upperRightLat, upperRightLon);
    }

}
