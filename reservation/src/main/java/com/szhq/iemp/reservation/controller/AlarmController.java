package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import com.szhq.iemp.reservation.api.model.NbiotDeviceRtAlarm;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.EsNbiotDeviceAlarmService;
import com.szhq.iemp.reservation.api.service.NbiotDeviceAlarmRtDataService;
import com.szhq.iemp.reservation.api.vo.AlarmCount;
import com.szhq.iemp.reservation.api.vo.query.AlarmQuery;
import com.szhq.iemp.reservation.service.AlarmServiceImpl;
import com.szhq.iemp.reservation.util.DecyptTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(value = "/alarm", description = "告警模块")
@RestController
@RequestMapping("/alarm")
@Slf4j
public class AlarmController {

	@Autowired
	private AlarmServiceImpl alarmService;
	@Autowired
	private NbiotDeviceAlarmRtDataService deviceAlarmRtDataService;
	@Autowired
	private ElectrmobileService electrombileService;
	@Autowired
	private EsNbiotDeviceAlarmService esNbiotDeviceAlarmService;
	@Value("${spring.profiles.active}")
	private String active;
	

	@ApiOperation(value = "告警查询-列表模式", notes = "告警查询-列表模式")
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result alarmlist(@RequestParam(value = "offset") Integer offset,
                            @RequestParam(value = "limit") Integer limit,
                            @RequestBody(required = false) AlarmQuery query) {
		Map<String, Object> map = new HashMap<>();
		MyPage<EsNbiotDeviceAlarm> pages = alarmService.esAlarmList(offset, limit, "ts", "desc", query);
		log.info("pages:" + pages.getContent().size());
		if(query != null && query.isApp()) {
			map.put("counts", pages.getContent());
			map.put("size", pages.getTotal());
			if(!"lc".equals(active)){
				electrombileService.setViewDateByImei(query.getImei(), new Date());
			}
			return new Result(ResultConstant.SUCCESS, map);
		}
		if(pages.getTotalPage() == 0) {
			map.put("counts", new ArrayList<>());
			map.put("size", 0);
			return new Result(ResultConstant.SUCCESS, map);
		}
		List<AlarmCount> counts = alarmService.esList(pages.getContent());
		map.put("counts", counts);
		map.put("size", pages.getTotal());
		return new Result(ResultConstant.SUCCESS, map);
	}

	@ApiOperation(value = "告警查询-地图模式", notes = "告警查询-地图模式")
	@RequestMapping(value = "/points", method = RequestMethod.GET)
	public Result getDeviceAlarmPoints(@RequestParam(value = "lowerLeft") String lowerLeft,
									   @RequestParam(value = "upperRight") String upperRight,
									   @RequestParam(value = "type",required = false) String type,
										HttpServletRequest request) {
		List<NbiotDeviceRtAlarm> list = new ArrayList<>();
		String[] lowerLefts = lowerLeft.split(",");
		String[] upperRights = upperRight.split(",");
		List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
		log.info("alarm-points-operatorIds:" + JSONObject.toJSONString(operatorIds));
		if(StringUtils.isEmpty(type)){
			if(operatorIds != null && operatorIds.get(0) != 0) {
				list = deviceAlarmRtDataService.getAlarmRealTimeDatas(operatorIds, Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]));
			}else {
				list = deviceAlarmRtDataService.getAlarmRealTimeDatas(Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]));
			}
		}else{
			if(operatorIds != null && operatorIds.get(0) != 0) {
				list = deviceAlarmRtDataService.getAlarmRealTimeDatas(type, operatorIds, Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]));
			}else {
				list = deviceAlarmRtDataService.getAlarmRealTimeDatas(type, Double.valueOf(lowerLefts[0]), Double.valueOf(lowerLefts[1]), Double.valueOf(upperRights[0]), Double.valueOf(upperRights[1]));
			}
		}
		return new Result(ResultConstant.SUCCESS, list);
	}
	
	@ApiOperation(value = "告警统计", notes = "告警统计")
	@RequestMapping(value = "/alarmStastic", method = RequestMethod.GET)
	public Result getAlarmStastic(AlarmQuery alarmQuery, HttpServletRequest request) {
		if(alarmQuery == null) {
			alarmQuery = new AlarmQuery();
		}
		List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
		log.info("alarmStastic-operatorIds:" + JSONObject.toJSONString(operatorIds));
		if(operatorIds != null && operatorIds.get(0) != 0) {
			alarmQuery.setOperatorIdList(operatorIds);
		}
		Map<Long, Long> map = alarmService.getAlarmStastic(alarmQuery);
		return new Result(ResultConstant.SUCCESS, map);
	}
	
	@ApiOperation(value = "告警电动车排名", notes = "告警电动车排名")
	@RequestMapping(value = "/elecAlarmSort", method = RequestMethod.GET)
	public Result elecAlarmSort(HttpServletRequest request) {
		List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
		log.info("elecAlarmSort-operatorIds:" + JSONObject.toJSONString(operatorIds));
		AlarmQuery alarmQuery = new AlarmQuery();
		if(operatorIds != null && operatorIds.get(0) != 0) {
			alarmQuery.setOperatorIdList(operatorIds);
		}
		Map<String, Long> map = alarmService.elecAlarmSort(alarmQuery);
		Map<String, Long> elecs = new HashMap<String, Long>();
		if(map != null && map.size() > 0) {
			for(String imei : map.keySet()) {
				if(electrombileService.findByImei(imei) != null) {
					elecs.put(electrombileService.findByImei(imei).getPlateNumber(), (Long)map.get(imei));
				}
			}
		}
		return new Result(ResultConstant.SUCCESS, elecs);
	}

	@ApiOperation(value = "某一设备某一时间后告警数量统计", notes = "某一设备某一时间后告警数量统计")
	@RequestMapping(value = "/alarmCountByImeiAndTime", method = RequestMethod.GET)
	public Result getAlarmCount(AlarmQuery alarmQuery, HttpServletRequest request) {
		if(alarmQuery == null) {
			alarmQuery = new AlarmQuery();
		}
		Long count = alarmService.elecAlarmCount(alarmQuery);
		return new Result(ResultConstant.SUCCESS, count);
	}
	
	@ApiOperation(value = "推送告警数据", notes = "推送告警数据")
	@RequestMapping(value = "/pushAlarmData", method = RequestMethod.POST)
	public Result pushAlarmData(@RequestBody String message) {
//		poundWebSocket.sendMessages(message);
		return new Result(ResultConstant.SUCCESS, "");
	}

	@ApiOperation(value = "根据imei删除告警数据", notes = "根据imei删除告警数据")
	@RequestMapping(value = "/deleteByImei", method = RequestMethod.DELETE)
	public Result deleteByImei(@RequestParam("imei") String imei) {
		Long count = esNbiotDeviceAlarmService.deleteByImei(imei);
		return new Result(ResultConstant.SUCCESS, count);
	}

}
