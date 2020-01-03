package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.TnoTrackerElec;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.NoTrackerElecmobileService;
import com.szhq.iemp.reservation.api.vo.DeviceBound;
import com.szhq.iemp.reservation.api.vo.NotrackerRegister;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(description = "添加车辆、绑定设备、解绑设备模块")
@RestController
@RequestMapping("/notracker")
@Slf4j
public class NoTrackerElecController {

    @Autowired
    private NoTrackerElecmobileService noTrackerElecmobileService;

    @ApiOperation(value = "列表查询", notes = "列表查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) ElecmobileQuery elecQuery) {
        log.info("notracker-query:" + JSONObject.toJSONString(elecQuery));
        MyPage<TnoTrackerElec> list = noTrackerElecmobileService.findAllByCretia(offset, limit, sort, order, elecQuery);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "添加车辆(有imei时备案,用户Id必填) ", notes = "添加车辆(有imei时备案,用户Id必填)")
    @RequestMapping(value = {"/addRegister", "/addRegisterNoImei"}, method = RequestMethod.POST)
    public Result addRegister(@RequestBody NotrackerRegister data, HttpServletRequest request, BindingResult result) {
        if (result.hasErrors() && result.getFieldError() != null) {
            String message = result.getFieldError().getDefaultMessage();
            log.error("addRegister error." + message);
            return new Result(ResultConstant.FAILED, message);
        }
        Long i = noTrackerElecmobileService.add(data, request);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "添加用户及车辆", notes = "添加用户及车辆")
    @RequestMapping(value = "/addUserAndElec", method = RequestMethod.POST)
    public Result addUserAndElec(@RequestBody NotrackerRegister data, HttpServletRequest request, BindingResult result) {
        if (result.hasErrors()) {
            if (result.getFieldError() != null) {
                String message = result.getFieldError().getDefaultMessage();
                log.error("addUserAndElec error." + message);
                return new Result(ResultConstant.FAILED, message);
            }
        }
        String userId = noTrackerElecmobileService.addUserAndElecmobile(data, request);
        return new Result(ResultConstant.SUCCESS, userId);
    }

    @ApiOperation(value = "绑定设备", notes = "绑定设备")
    @RequestMapping(value = "/boundDevices", method = RequestMethod.POST)
    public Result boundDevices(@Valid @RequestBody DeviceBound deviceBound, BindingResult result) {
        if (result.hasErrors() && result.getFieldError() != null) {
            String message = result.getFieldError().getDefaultMessage();
            log.error("boundDevice error." + message);
            return new Result(ResultConstant.FAILED, message);
        }
        Long id = noTrackerElecmobileService.boundDevice(deviceBound);
        return new Result(ResultConstant.SUCCESS, id);
    }

    @ApiOperation(value = "绑定设备(APP)", notes = "绑定设备")
    @RequestMapping(value = "/boundDevice", method = RequestMethod.POST)
    public Result boundDeviceApp(@RequestParam("userId") String userId,
                                 @RequestParam("imei") String imei,
                                 @RequestParam("deviceName") String name,
                                 @RequestParam(value = "frequency", defaultValue = "86400") Integer frequency,
                                 @RequestParam(required = false, value = "plateNo") String plateNo,
                                 @RequestParam(required = false, value = "type") String type) {
        DeviceBound deviceBound = new DeviceBound();
        deviceBound.setDeviceName(name);
        deviceBound.setFrequency(frequency);
        deviceBound.setImei(imei);
        deviceBound.setPlateNumber(plateNo);
        deviceBound.setType(type);
        deviceBound.setUserId(userId);
        noTrackerElecmobileService.boundDevice(deviceBound);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "解绑设备(310)", notes = "解绑设备(310)")
    @RequestMapping(value = "/unboundDevice", method = RequestMethod.POST)
    public Result unboundDevice(@RequestParam("imei") String imei) {
        noTrackerElecmobileService.unBoundDevice(imei);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "解绑(无设备时删除电动车,有设备时都删除)", notes = "无设备时删除电动车,有设备时都删除")
    @RequestMapping(value = "/unbound", method = RequestMethod.POST)
    public Result unbound(@RequestParam("userId") String userId, @RequestParam("elecId") Long elecId) {
        noTrackerElecmobileService.unBoundByElecIdAndUserId(userId, elecId);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "解绑设备(不删除电动车)", notes = "解绑设备(不删除电动车)")
    @RequestMapping(value = "/unboundDeviceNoDelElec", method = RequestMethod.POST)
    public Result unboundDeviceNoDelElec(@RequestParam("imei") String imei) {
        noTrackerElecmobileService.unboundDeviceNoDeleteElec(imei);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "删除缓存", notes = "删除缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis() {
        Integer count = noTrackerElecmobileService.deleteNoTrackerRedisKey();
        return new Result(ResultConstant.SUCCESS, count);
    }

}
