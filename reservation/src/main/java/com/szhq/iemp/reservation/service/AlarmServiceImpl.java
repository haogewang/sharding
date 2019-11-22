package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tregistration;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.EsNbiotDeviceAlarmService;
import com.szhq.iemp.reservation.api.service.RegistrationService;
import com.szhq.iemp.reservation.api.service.UserService;
import com.szhq.iemp.reservation.api.vo.AlarmCount;
import com.szhq.iemp.reservation.api.vo.query.AlarmQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
public class AlarmServiceImpl {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private EsNbiotDeviceAlarmService esNbiotDeviceAlarmService;


    public MyPage<EsNbiotDeviceAlarm> esAlarmList(Integer page, Integer size, String sorts, String orders, AlarmQuery alarmQuery) {
        MyPage<EsNbiotDeviceAlarm> pages = esNbiotDeviceAlarmService.alarmList(page, size, sorts, orders, alarmQuery);
        return pages;
    }

    public List<AlarmCount> esList(List<EsNbiotDeviceAlarm> alarms) {
        List<AlarmCount> alarmCounts = new ArrayList<>();
        for (EsNbiotDeviceAlarm alarm : alarms) {
            if (alarm.getImei() == null) {
                continue;
            }
            Tregistration register = registrationService.findByImei(alarm.getImei());
            if (register == null) {
                log.error("es imei:" + alarm.getImei());
                Long count = esNbiotDeviceAlarmService.deleteByImei(alarm.getImei());
                log.error("delete es imei count:" + count);
                continue;
            }
            Tuser user = userService.findById(register.getUserId());
            Telectrmobile elec = electrombileService.findByElecId(register.getElectrmobileId());
            AlarmCount alarmCount = new AlarmCount();
            alarmCount.setEsAlarm(alarm);
            alarmCount.setElec(elec);
            alarmCount.setUser(user);
            alarmCounts.add(alarmCount);
        }
        return alarmCounts;
    }

    public Map<Long, Long> getAlarmStastic(AlarmQuery alarmQuery) {
        Map<Long, Long> map = esNbiotDeviceAlarmService.alarmStastic(alarmQuery);
        return map;
    }

    public Map<String, Long> elecAlarmSort(AlarmQuery alarmQuery) {
        Map<String, Long> map = esNbiotDeviceAlarmService.elecAlarmSort(alarmQuery);
        map = sortByValue(map);
        return map;
    }

    public Long elecAlarmCount(AlarmQuery alarmQuery) {
        Long count = esNbiotDeviceAlarmService.elecAlarmCount(alarmQuery);
        return count;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Entry<K, V>> st = map.entrySet().stream();
        st.sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> result.put(ele.getKey(), ele.getValue()));
//        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
