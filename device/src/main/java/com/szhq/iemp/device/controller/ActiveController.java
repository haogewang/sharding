package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TactiveInfo;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.model.TuserGroup;
import com.szhq.iemp.device.api.service.ActiveInfoService;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.service.UserGroupService;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.ActiveQuery;
import com.szhq.iemp.device.api.vo.query.SaleRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Api(description = "激活设备模块")
@RestController
@RequestMapping("/activator")
public class ActiveController {

    @Autowired
    private ActiveInfoService activeInfoService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private UserGroupService userGroupService;

    @ApiOperation(value = "激活设备", notes = "激活设备")
    @RequestMapping(value = "/active", method = RequestMethod.POST)
    public Result active(@RequestParam(value = "imei") String imei,
                         @RequestParam(value = "userId") String userId) {
        Integer i = activeInfoService.activeImei(imei, userId);
        if(i > 0){
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, "");
    }

    @ApiOperation(value = "统计激活人员今日激活设备数", notes = "统计激活人员今日激活设备数")
    @RequestMapping(value = "/countTodayActive", method = RequestMethod.POST)
    public Result countTodayActive(@RequestParam(value = "userId") String userId) {
        Integer i = activeInfoService.countTodayActiveByActivatorId(userId, 1);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "统计激活人员今日退货设备数", notes = "统计激活人员今日退货设备数")
    @RequestMapping(value = "/countTodayBack", method = RequestMethod.POST)
    public Result countTodayBack(@RequestParam(value = "userId") String userId) {
        Integer i = activeInfoService.countTodayActiveByActivatorId(userId, 0);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "历史激活(退货)设备记录", notes = "历史激活设备记录")
    @RequestMapping(value = "/historyActiveCount", method = RequestMethod.POST)
    public Result historyActiveCount(@RequestParam(value = "offset") Integer offset,
                                     @RequestParam(value = "pagesize") Integer limit,
                                     @RequestParam(required = false, value = "sort") String sort,
                                     @RequestParam(required = false, value = "order") String order,
                                     @RequestBody(required = false) ActiveQuery query) {
        Map<String, Object> result = new HashMap<>();
        MyPage<TactiveInfo> list = activeInfoService.findAllByCriteria(offset,limit, sort, order, query);
        result.put("actives", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "设备去激活", notes = "设备去激活")
    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public Result back(@RequestParam(value = "imei") String imei,
                         @RequestParam(value = "userId") String userId) {
        Integer i = activeInfoService.back(imei, userId);
        if(i > 0){
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, "");
    }

//    @ApiOperation(value = "设备退货", notes = "设备退货")
//    @RequestMapping(value = "/returnOff", method = RequestMethod.POST)
//    public Result returnOff(@RequestParam(value = "imei") String imei) {
//        Integer i = activeInfoService.returnOff(imei);
//        if(i > 0){
//            return new Result(ResultConstant.SUCCESS, i);
//        }
//        return new Result(ResultConstant.FAILED, "");
//    }


    @ApiOperation(value = "根据用户Id查询设备激活时间", notes = "根据用户Id查询设备激活时间")
    @RequestMapping(value = "/getActiveByUserId", method = RequestMethod.POST)
    public Result getActiveByUserId(@RequestParam(value = "userId") String userId) {
        List<TactiveInfo> list = activeInfoService.getActiveByUserId(userId);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "通过运营公司id查找下级子运营公司详情", notes = "通过运营公司id查找下级子类详情")
    @RequestMapping(value = "/getOperatorInfoByPId", method = RequestMethod.POST)
    public Result getOperatorByParentId(@RequestParam(value = "pid") Integer id,
                                        @RequestBody ActiveQuery query) {
        List<Toperator> operators = operatorService.findByParent(id);
        List<Toperator> result = new ArrayList<>();
        if(operators != null && !operators.isEmpty()){
            for(Toperator operator : operators){
                List<Integer> ids =  operatorService.findAllChildIds(operator.getId());
                Integer activeCount = activeInfoService.countActiveByOperatorIds(ids, 1, query.getStartTime(), query.getEndTime());
                Integer unActiveCount = activeInfoService.countActiveByOperatorIds(ids, 0, query.getStartTime(), query.getEndTime());
                Integer storeCount = deviceInventoryService.countStoreCountByOperatorIds(ids);
                operator.setDeviceCount(storeCount);
                operator.setActiveCount(activeCount);
                operator.setUnActiveCount(unActiveCount);
                result.add(operator);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("operators", result);
        return new Result(ResultConstant.SUCCESS, map);
    }

    @ApiOperation(value = "通过运营公司id查找组详情", notes = "通过运营公司id查找组详情")
    @RequestMapping(value = "/getGroupInfoByOId", method = RequestMethod.POST)
    public Result getGroupByOId(@RequestParam(value = "id" ) Integer id,
                                @RequestBody ActiveQuery query) {
        List<TuserGroup> result = new ArrayList<>();
        List<TuserGroup> userGroups =  userGroupService.findByOperatorIdIn(Arrays.asList(id));
        if(userGroups != null && !userGroups.isEmpty()){
            for(TuserGroup group : userGroups){
                Integer activeCount = activeInfoService.countActiveByGroupIds(Arrays.asList(group.getId()), 1, query.getStartTime(), query.getEndTime());
                Integer unActiveCount = activeInfoService.countActiveByGroupIds(Arrays.asList(group.getId()), 0, query.getStartTime(), query.getEndTime());
                group.setActiveCount(activeCount);
                group.setUnActiveCount(unActiveCount);
                result.add(group);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("groups", result);
        return new Result(ResultConstant.SUCCESS, map);
    }

    @ApiOperation(value = "根据运营公司Id按天进行数量统计", notes = "根据运营公司Id按天进行数量统计")
    @RequestMapping(value = "/activeStatisticByOperatorId", method = RequestMethod.POST)
    public Result activeStatisticByOperatorId(@RequestBody SaleRecordQuery query) {
        List<ActiveDeviceCount> result = activeInfoService.activeStatisticByOperatorId(query);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据组Id按天进行数量统计", notes = "根据组Id按天进行数量统计")
    @RequestMapping(value = "/activeStatisticByGroupId", method = RequestMethod.POST)
    public Result activeStatisticByGroupId(@RequestBody SaleRecordQuery query) {
        List<ActiveDeviceCount> result = activeInfoService.activeStatisticByGroupId(query);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据组Id查看详情", notes = "根据组Id查看详情")
    @RequestMapping(value = "/getInfoByGroupId", method = RequestMethod.POST)
    public Result getInfoByGroupId(@RequestParam(value = "groupId") String id) {
        List<ActiveDeviceCount> result = activeInfoService.getStatisticInfoByGroupId(id);
        return new Result(ResultConstant.SUCCESS, result);
    }
}
