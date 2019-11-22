package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.reservation.api.model.NbiotDeviceRtData;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.EsNbiotDeviceAlarmService;
import com.szhq.iemp.reservation.api.service.NbiotDeviceRtDataService;
import com.szhq.iemp.reservation.api.vo.NbiotRtDataVo;
import com.szhq.iemp.reservation.api.vo.query.AlarmQuery;
import com.szhq.iemp.reservation.repository.TnbiotDeviceRtDataRepository;
import com.szhq.iemp.reservation.util.GpsTransferUtil;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@Service
@Transactional
@Slf4j
public class NbiotDeviceRtDataServiceImpl implements NbiotDeviceRtDataService {

    @Resource
    private TnbiotDeviceRtDataRepository deviceRtDataRepository;
    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private EsNbiotDeviceAlarmService esNbiotDeviceAlarmService;

    @Resource(name = "secondRedisUtil")
    private RedisUtil secondRedisUtil;

    private GpsTransferUtil gpsTransferUtil = new GpsTransferUtil();

    @Override
    public NbiotDeviceRtData findByImei(String imei, List<Integer> operatorIds) {
        NbiotDeviceRtData nbiotDeviceRtData = new NbiotDeviceRtData();
        log.info("rtdata operatorIds:" + JSONObject.toJSONString(operatorIds));
        if (operatorIds != null && operatorIds.get(0) != 0) {
            nbiotDeviceRtData = deviceRtDataRepository.findByImei(imei, operatorIds);
        } else {
            nbiotDeviceRtData = deviceRtDataRepository.findByImei(imei);
        }
        return nbiotDeviceRtData;
    }

    @Override
    public List<NbiotDeviceRtData> findByImeiIn(List<Telectrmobile> elecs) {
        List<NbiotDeviceRtData> result = new ArrayList<>();
        for (Telectrmobile elec : elecs) {
            NbiotDeviceRtData rtData = deviceRtDataRepository.findByImei(elec.getImei());
            if (rtData == null) {
                NbiotDeviceRtData rtdata = new NbiotDeviceRtData();
                rtdata.setImei(elec.getImei());
                rtdata.setDeviceName(elec.getName());
                rtdata.setCreateTime(elec.getCreateTime());
                rtdata.setFrequency(elec.getFrequency());
                result.add(rtdata);
            } else {
                rtData.setDeviceName(elec.getName());
                rtData.setCreateTime(elec.getCreateTime());
                rtData.setFrequency(elec.getFrequency());
                if (StringUtils.isNotEmpty(rtData.getGps()) && StringUtils.isNotEmpty(rtData.getWlanInfo())) {
                    JSONObject gps = JSONObject.parseObject(rtData.getGps());
                    JSONObject wlanInfo = JSONObject.parseObject(rtData.getWlanInfo());
                    String gpsTime = gps.getString("time");
                    String wlanInfoTime = wlanInfo.getJSONObject("wlan").getString("time");
                    log.info("gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                    if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) > Long.valueOf(wlanInfoTime)) {
                        rtData.setWlanInfo(null);
                    } else if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) < Long.valueOf(wlanInfoTime)) {
                        rtData.setGps(null);
                    } else {
                        log.error("gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                        rtData.setWlanInfo(null);
                    }
                }
                result.add(rtData);
            }
        }
        return result;
    }

    @Override
    public List<NbiotDeviceRtData> findByImeis(List<String> imeis) {
        return deviceRtDataRepository.findByImeis(imeis);
    }

    @Override
    public List<NbiotRtDataVo> findRtDataByElecs(List<Telectrmobile> elecs) {
        List<NbiotRtDataVo> result = new ArrayList<>();
        for (Telectrmobile elec : elecs) {
            AlarmQuery alarmQuery = new AlarmQuery();
            if (elec.getViewDate() != null) {
                alarmQuery.setStartTimestamp(elec.getViewDate().getTime());
            }
            alarmQuery.setImei(elec.getImei());
            Long count = esNbiotDeviceAlarmService.elecAlarmCount(alarmQuery);
            NbiotDeviceRtData rtData = deviceRtDataRepository.findByImei(elec.getImei());
            NbiotRtDataVo rtDataVo = new NbiotRtDataVo();
            if (rtData == null) {
                rtDataVo.setImei(elec.getImei());
                rtDataVo.setDeviceName(elec.getName());
                rtDataVo.setFrequency(elec.getFrequency());
                rtDataVo.setCreateTime(elec.getCreateTime());
                rtDataVo.setUnReadAlarmCount(count);
                result.add(rtDataVo);
            } else {
                rtDataVo.setImei(elec.getImei());
                rtDataVo.setDeviceName(elec.getName());
                rtDataVo.setFrequency(elec.getFrequency());
                rtDataVo.setCreateTime(elec.getCreateTime());
                rtDataVo.setUnReadAlarmCount(count);
                if (StringUtils.isNotEmpty(rtData.getGps()) && StringUtils.isNotEmpty(rtData.getWlanInfo())) {
                    JSONObject gps = JSONObject.parseObject(rtData.getGps());
                    JSONObject wlanInfo = JSONObject.parseObject(rtData.getWlanInfo());
                    String gpsTime = gps.getString("time");
                    String wlanInfoTime = wlanInfo.getJSONObject("wlan").getString("time");
//					log.info("gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                    if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) > Long.valueOf(wlanInfoTime)) {
                        rtDataVo.setTime(Long.valueOf(gpsTime));
                        rtDataVo.setDeviceVoltper(gps.getString("deviceVoltper"));
                        rtDataVo.setDeviceVolt(String.valueOf(gps.getDouble("deviceVolt")));
                        rtDataVo.setLat(gps.getDouble("lat"));
                        rtDataVo.setLon(gps.getDouble("lon"));
                        rtDataVo.setDeviceMode(gps.getString("deviceMode"));
                        rtDataVo.setType("GPS");
                    } else if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) < Long.valueOf(wlanInfoTime)) {
                        JSONObject wlanJson = wlanInfo.getJSONObject("wlan");
                        rtDataVo.setTime(Long.valueOf(wlanInfoTime));
                        rtDataVo.setDeviceVoltper(wlanJson.getString("deviceVoltper"));
                        rtDataVo.setDeviceVolt(String.valueOf(wlanJson.getDouble("deviceVolt")));
                        if (wlanInfo.containsKey("location") && StringUtils.isNotEmpty(wlanInfo.getString("location"))) {
                            String location = wlanInfo.getString("location");
                            JSONObject locationJson = JSONObject.parseObject(location);
                            Double lat = locationJson.getDouble("latitude");
                            Double lon = locationJson.getDouble("longitude");
                            Map<String, Double> map = gpsTransferUtil.transfer(lat, lon);
//							log.info("lat:{},lon:{}, transferlat:{},transferlon:{}",lat, lon, map.get("lat"), map.get("lon"));
                            rtDataVo.setLat(map.get("lat"));
                            rtDataVo.setLon(map.get("lon"));
                            rtDataVo.setAddress(locationJson.getString("addressDescription"));
                        }
                        rtDataVo.setDeviceMode(wlanJson.getString("deviceMode"));
                        rtDataVo.setType("Wi-Fi");
                    } else {
                        log.error("wrong data.gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                    }
                    result.add(rtDataVo);
                } else if (StringUtils.isNotEmpty(rtData.getGps()) && StringUtils.isEmpty(rtData.getWlanInfo())) {
                    JSONObject gps = JSONObject.parseObject(rtData.getGps());
                    String gpsTime = gps.getString("time");
                    rtDataVo.setImei(elec.getImei());
                    rtDataVo.setDeviceName(elec.getName());
                    rtDataVo.setFrequency(elec.getFrequency());
                    rtDataVo.setTime(Long.valueOf(gpsTime));
                    rtDataVo.setDeviceVoltper(gps.getString("deviceVoltper"));
                    rtDataVo.setDeviceVolt(String.valueOf(gps.getDouble("deviceVolt")));
                    rtDataVo.setLat(gps.getDouble("lat"));
                    rtDataVo.setLon(gps.getDouble("lon"));
                    rtDataVo.setDeviceMode(gps.getString("deviceMode"));
                    rtDataVo.setType("GPS");
                    result.add(rtDataVo);
                } else if (StringUtils.isEmpty(rtData.getGps()) && StringUtils.isNotEmpty(rtData.getWlanInfo())) {
                    JSONObject wlanInfo = JSONObject.parseObject(rtData.getWlanInfo());
                    JSONObject wlanJson = wlanInfo.getJSONObject("wlan");
                    String wlanInfoTime = wlanJson.getString("time");
                    rtDataVo.setTime(Long.valueOf(wlanInfoTime));
                    rtDataVo.setDeviceVoltper(wlanJson.getString("deviceVoltper"));
                    rtDataVo.setDeviceVolt(String.valueOf(wlanJson.getDouble("deviceVolt")));
                    if (wlanInfo.containsKey("location") && StringUtils.isNotEmpty(wlanInfo.getString("location"))) {
                        String location = wlanInfo.getString("location");
                        JSONObject locationJson = JSONObject.parseObject(location);
                        Double lat = locationJson.getDouble("latitude");
                        Double lon = locationJson.getDouble("longitude");
                        Map<String, Double> map = gpsTransferUtil.transfer(lat, lon);
//						log.info("lat:{},lon:{}, transferlat:{},transferlon:{}",lat, lon, map.get("lat"), map.get("lon"));
                        rtDataVo.setLat(map.get("lat"));
                        rtDataVo.setLon(map.get("lon"));
                        rtDataVo.setAddress(locationJson.getString("addressDescription"));
                    }
                    rtDataVo.setDeviceMode(wlanJson.getString("deviceMode"));
                    rtDataVo.setType("Wi-Fi");
                    result.add(rtDataVo);
                }
            }
        }
        return result;
    }

    @Override
    public NbiotDeviceRtData findByImei(String imei) {
        Telectrmobile elec = electrombileService.findByImei(imei);
        if (elec == null) {
            log.error("no imei found.imei:" + imei);
            return null;
        }
        NbiotDeviceRtData rtData = deviceRtDataRepository.findByImei(imei);
        if (rtData == null) {
            NbiotDeviceRtData rtdata = new NbiotDeviceRtData();
            rtdata.setImei(elec.getImei());
            rtdata.setDeviceName(elec.getName());
            rtdata.setCreateTime(elec.getCreateTime());
            rtdata.setFrequency(elec.getFrequency());
            return rtData;
        } else {
            rtData.setDeviceName(elec.getName());
            rtData.setCreateTime(elec.getCreateTime());
            rtData.setFrequency(elec.getFrequency());
            if (StringUtils.isNotEmpty(rtData.getGps()) && StringUtils.isNotEmpty(rtData.getWlanInfo())) {
                JSONObject gps = JSONObject.parseObject(rtData.getGps());
                JSONObject wlanInfo = JSONObject.parseObject(rtData.getWlanInfo());
                String gpsTime = gps.getString("time");
                String wlanInfoTime = wlanInfo.getJSONObject("wlan").getString("time");
                log.info("gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) > Long.valueOf(wlanInfoTime)) {
                    rtData.setWlanInfo(null);
                } else if (StringUtils.isNotEmpty(gpsTime) && StringUtils.isNotEmpty(wlanInfoTime) && Long.valueOf(gpsTime) < Long.valueOf(wlanInfoTime)) {
                    rtData.setGps(null);
                } else {
                    log.error("gpsTime:{},wlanInfoTime:{}", gpsTime, wlanInfoTime);
                    rtData.setWlanInfo(null);
                }
            }
            return rtData;
        }
    }

    @Override
    public List<NbiotDeviceRtData> getData(String lowerLeft, String upperRight, List<Integer> operatorIds) {
        List<NbiotDeviceRtData> nbiotDeviceData = new ArrayList<NbiotDeviceRtData>();
        String[] lowerLefts = lowerLeft.split(",");
        String[] upperRights = upperRight.split(",");
        log.info("rtdata operatorIds:" + JSONObject.toJSONString(operatorIds));
        if (operatorIds != null) {
            nbiotDeviceData = deviceRtDataRepository.selectData(Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]), operatorIds);
        } else {
            nbiotDeviceData = deviceRtDataRepository.selectData(Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]));
        }
        return nbiotDeviceData;
    }

    @Override
    public NbiotDeviceRtData getLocation(String imei) {
        return findByImei(imei);
    }

    @Override
    public Integer deleteByImei(String imei) {
        deviceRtDataRepository.deleteByImei(imei);
        log.info("delete NbiotDeviceData, imei:" + imei);
        return null;
    }

    @Override
    public Integer save(List<NbiotDeviceRtData> entities) {
        List<NbiotDeviceRtData> list = new ArrayList<>();
        list = deviceRtDataRepository.saveAll(entities);
        return list.size();
    }

    public NbiotDeviceRtData dataTransfer(NbiotDeviceRtData rtData){
        if (StringUtils.isEmpty(rtData.getGps()) && StringUtils.isNotEmpty(rtData.getWlanInfo())) {
            JSONObject wlanInfo = JSONObject.parseObject(rtData.getWlanInfo());
            if (wlanInfo.containsKey("location") && StringUtils.isNotEmpty(wlanInfo.getString("location"))) {
                String location = wlanInfo.getString("location");
                JSONObject locationJson = JSONObject.parseObject(location);
                Double lat = locationJson.getDouble("latitude");
                Double lon = locationJson.getDouble("longitude");
                Map<String, Double> map = gpsTransferUtil.transfer(lat, lon);
                locationJson.put("latitude", map.get("lat"));
                locationJson.put("longitude", map.get("lon"));
                wlanInfo.put("location", locationJson.toJSONString());
                rtData.setWlanInfo(wlanInfo.toJSONString());
                return rtData;
            }
        }
        return rtData;
    }

    public static void main(String[] args) {
        String gps = "{\\\"alert\\\":36,\\\"alt\\\":43,\\\"bear\\\":0,\\\"lat\\\":34.212574,\\\"lon\\\":108.838853,\\\"speed\\\":0,\\\"temp\\\":26,\\\"time\\\":1562768568001}";
        gps = StringEscapeUtils.unescapeJava(gps);
        JSONObject json = JSONObject.parseObject(gps);
        System.out.println(json);

    }
}
