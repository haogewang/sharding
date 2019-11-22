package com.szhq.iemp.reservation.controller;


import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.HistoryWlanData;
import com.szhq.iemp.reservation.api.model.NbiotDeviceData;
import com.szhq.iemp.reservation.api.model.NbiotDeviceRtData;
import com.szhq.iemp.reservation.api.service.NbiotDeviceDataService;
import com.szhq.iemp.reservation.api.service.NbiotDeviceRtDataService;
import com.szhq.iemp.reservation.api.service.NbiotHistoryWlanDataService;
import com.szhq.iemp.reservation.api.vo.query.DateQuery;
import com.szhq.iemp.reservation.util.DecyptTokenUtil;
import com.szhq.iemp.reservation.util.GpsTransferUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 车辆定位模块接口
 * @author wanghao
 */
@Api(value = "/device/data", description = "车辆定位(地图)模块")
@RequestMapping("/device/data")
@RestController
public class DeviceDataController {

    @Autowired
    private NbiotDeviceRtDataService nbiotDeviceRtDataService;
    @Autowired
    private NbiotDeviceDataService nbiotDeviceDataService;
    @Autowired
    private NbiotHistoryWlanDataService nbiotHistoryWlanDataService;

    private GpsTransferUtil gpsTransferUtil = new GpsTransferUtil();

    @ApiOperation(value = "获取某一设备的实时位置", notes = "获取某一设备的实时位置")
    @GetMapping(path = "/location")
    public Result getLocation(@RequestParam(value = "imei") String imei) {
        NbiotDeviceRtData data = nbiotDeviceRtDataService.getLocation(imei);
        return new Result(ResultConstant.SUCCESS, data);
    }

    @ApiOperation(value = "显示桌面的电动车", notes = "显示桌面的电动车")
    @GetMapping(path = "/map")
    public Result getRtDatas(@RequestParam(value = "lowerLeft") String lowerLeft,
                             @RequestParam(value = "upperRight") String upperRight,
                             @RequestParam(value = "type", required = false) String type, HttpServletRequest request) {
    	List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
        List<NbiotDeviceRtData> nbiotDeviceData = nbiotDeviceRtDataService.getData(lowerLeft, upperRight, operatorIds);
        return new Result(ResultConstant.SUCCESS, nbiotDeviceData);
    }

    /**
     * 查cassandra某一设备在某一时间段的数据
     */
    @ApiOperation(value = "查某一设备在某一时间段的数据", notes = "查某一设备在某一时间段的数据")
    @GetMapping(value = "/ts")
    public Result getData(@RequestParam(value = "imei") String imei,
                            @RequestParam(value = "start") Long start,
                            @RequestParam(value = "end") Long end,
                            @RequestParam(value = "field",required = false) String field) {
        List<NbiotDeviceData> list = nbiotDeviceDataService.getData(imei, start, end);
        Map<String, Object> result = new HashMap<>();
        if(StringUtils.isNotEmpty(field)) {
			List<Object> lists = new ArrayList<Object>();
			if(list != null && list.size() > 0) {
				for(NbiotDeviceData data : list) {
					JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(data));
					Object o = json.get(field);
					lists.add(o);
				}
				result.put("location", lists);
				return new Result(ResultConstant.SUCCESS, result);
			}
			return new Result(ResultConstant.SUCCESS, result);
		}
        result.put("location", list);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "查某一设备最近的数据", notes = "查某一设备在最近的数据")
    @GetMapping(value = "/latestdata")
    public Result getLatestData(@RequestParam(value = "imei") String imei,
                                @RequestParam(value = "limit",required = false, defaultValue = "50") Integer limit) {
        List<NbiotDeviceData> gpslist = nbiotDeviceDataService.getData(imei, limit);
        List<HistoryWlanData> wlanlist = nbiotHistoryWlanDataService.getData(imei, limit);
        Map<String, Object> result = new HashMap<>();
        List<NbiotDeviceRtData> ds = new ArrayList<>();
        if(gpslist != null && !gpslist.isEmpty()){
            for(NbiotDeviceData data : gpslist){
                NbiotDeviceRtData history = new NbiotDeviceRtData();
                String gps = data.getGps();
                Date ts = data.getTrackerDataPK().getTs();
                history.setGps(gps);
                history.setCreateTime(ts);
                history.setType("GPS");
                ds.add(history);
            }
        }
        if(wlanlist != null && !wlanlist.isEmpty()){
            for(HistoryWlanData data : wlanlist){
                NbiotDeviceRtData history = new NbiotDeviceRtData();
                String wlan = data.getLocation();
                Date ts = data.getTrackerDataPK().getTs();
                if(StringUtils.isNotEmpty(wlan)){
                    JSONObject wlanJSON = JSONObject.parseObject(wlan);
                    Double lat = wlanJSON.getDouble("latitude");
                    Double lon = wlanJSON.getDouble("longitude");
                    Map<String, Double> map = gpsTransferUtil.transfer(lat, lon);
                    wlanJSON.put("latitude", map.get("lat"));
                    wlanJSON.put("longitude",  map.get("lon"));
                    history.setWlanInfo(wlanJSON.toJSONString());
                }
                history.setCreateTime(ts);
                history.setType("WLAN");
                ds.add(history);
            }
        }
        ds = ds.stream().sorted(Comparator.comparing(NbiotDeviceRtData::getCreateTime).reversed()).collect(Collectors.toList());
        if(ds.size() > limit){
            ds = ds.subList(0, limit);
            result.put("location", ds);
        }else{
            result.put("location", ds);
        }
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "查某一设备某几个时间段的数据", notes = "查某一设备某几个时间段的数据")
    @PostMapping(value = "/getDataByTimeQuery")
    public Result getDataByDateQuery(@RequestParam(value = "imei") String imei,
                                     @RequestBody List<DateQuery> dates) {
        List<NbiotDeviceRtData> result = new ArrayList<>();
        for(DateQuery query : dates){
            NbiotDeviceRtData history = new NbiotDeviceRtData();
            NbiotDeviceData gps = nbiotDeviceDataService.getDataByDateQuery(imei, query);
            HistoryWlanData wlan = nbiotHistoryWlanDataService.getDataByDateQuery(imei, query);
            if(gps != null && wlan != null){
                if(gps.getTrackerDataPK().getTs().before(wlan.getTrackerDataPK().getTs())) {
                    String location = gps.getGps();
                    Date ts = gps.getTrackerDataPK().getTs();
                    history.setGps(location);
                    history.setCreateTime(ts);
                    history.setType("GPS");
                    result.add(history);
                }
            }
            else if(gps != null){
                String location = gps.getGps();
                Date ts = gps.getTrackerDataPK().getTs();
                history.setGps(location);
                history.setCreateTime(ts);
                history.setType("GPS");
                result.add(history);
            }
            else if(wlan != null){
                String location = wlan.getLocation();
                Date ts = wlan.getTrackerDataPK().getTs();
                if(StringUtils.isNotEmpty(location)){
                    JSONObject wlanJSON = JSONObject.parseObject(location);
                    Double lat = wlanJSON.getDouble("latitude");
                    Double lon = wlanJSON.getDouble("longitude");
                    Map<String, Double> map = gpsTransferUtil.transfer(lat, lon);
                    wlanJSON.put("latitude", map.get("lat"));
                    wlanJSON.put("longitude",  map.get("lon"));
                    history.setWlanInfo(wlanJSON.toJSONString());
                }
                history.setCreateTime(ts);
                history.setType("WLAN");
                result.add(history);
            }
        }
        return new Result(ResultConstant.SUCCESS, result);
    }

}