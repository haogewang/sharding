package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TpolicePrecinct;
import com.szhq.iemp.device.api.service.PolicePrecinctService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "派出所模块")
@RestController
@RequestMapping("/police")
public class PoliceStationController {

    @Autowired
    private PolicePrecinctService policeStationService;

    @ApiOperation(value = "派出所列表", notes = "派出所列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list() {
        List<TpolicePrecinct> list = policeStationService.findAll();
        if (list != null) {
            return new Result(ResultConstant.SUCCESS, list);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "派出所添加", notes = "派出所添加")
    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public Result add(@RequestBody TpolicePrecinct entity) {
        TpolicePrecinct policeStation = policeStationService.add(entity);
        if (policeStation != null) {
            return new Result(ResultConstant.SUCCESS, policeStation);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "派出所删除", notes = "派出所删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result delete(@RequestParam("id") String id) {
        policeStationService.deleteById(id);
        return new Result(ResultConstant.SUCCESS, 1);
    }

}
