package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceState;
import com.szhq.iemp.device.api.service.DeviceStateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(description = "设备状态模块")
@RestController
@Slf4j
@RequestMapping("/state")
public class DeviceStateController {

    @Autowired
    private DeviceStateService deviceStateService;

    @ApiOperation(value = "根据imeis查找设备状态", notes = "根据imeis查找设备状态")
    @RequestMapping(value = "/getStatesByImei", method = RequestMethod.POST)
    public Result search(@RequestBody List<String> imeis) {
        Map<String, TdeviceState> map = deviceStateService.getStateByImeis(imeis);
        return new Result(ResultConstant.SUCCESS, map);
    }

}
