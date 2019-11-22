package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.model.TuserGroup;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.ActiveQuery;
import com.szhq.iemp.device.api.vo.query.SaleRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(description = "销售记录模块")
@RestController
@RequestMapping("/saleRecord")
@Slf4j
public class SaleRecordController {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private SaleRecordService saleRecordService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private UserGroupService userGroupService;

//    @ApiOperation(value = "根据运营公司Id按天进行数量统计", notes = "根据运营公司Id按天进行数量统计")
//    @RequestMapping(value = "/saleStatisticByOperatorId", method = RequestMethod.POST)
//    public Result saleRecordStatisticByOperatorId(@RequestBody SaleRecordQuery query) {
//        List<ActiveDeviceCount> result = saleRecordService.saleStatisticByOperatorId(query);
//        return new Result(ResultConstant.SUCCESS, result);
//    }
//
//    @ApiOperation(value = "根据组Id按天进行数量统计", notes = "根据组Id按天进行数量统计")
//    @RequestMapping(value = "/saleStatisticByGroupId", method = RequestMethod.POST)
//    public Result saleRecordStatisticByGroupId(@RequestBody SaleRecordQuery query) {
//        List<ActiveDeviceCount> result = saleRecordService.saleStatisticByGroupId(query);
//        return new Result(ResultConstant.SUCCESS, result);
//    }
//
//    @ApiOperation(value = "通过运营公司id查找下级子运营公司详情", notes = "通过运营公司id查找下级子运营公司详情")
//    @RequestMapping(value = "/getOperatorInfoByPId", method = RequestMethod.POST)
//    public Result getOperatorByParentId(@RequestParam(value = "pid") Integer id,
//                                        @RequestBody SaleRecordQuery query) {
//        List<Toperator> operators = operatorService.findByParent(id);
//        List<Toperator> result = new ArrayList<>();
//        if(operators != null && !operators.isEmpty()){
//            for(Toperator operator : operators){
//                List<Integer> operatorIds =  operatorService.findAllChildIds(operator.getId());
//                Integer backCount = saleRecordService.countSaleByOperatorIds(operatorIds, 0, query);
//                Integer saleCount = saleRecordService.countSaleByOperatorIds(operatorIds, 1, query);
//                Integer storeCount = deviceInventoryService.countStoreCountByOperatorIds(operatorIds);
//                operator.setDeviceCount(storeCount);
//                operator.setActiveCount(saleCount);
//                operator.setUnActiveCount(backCount);
//                result.add(operator);
//            }
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("operators", result);
//        return new Result(ResultConstant.SUCCESS, map);
//    }
//
//    @ApiOperation(value = "通过运营公司id查找组详情", notes = "通过运营公司id查找组详情")
//    @RequestMapping(value = "/getGroupInfoByOId", method = RequestMethod.POST)
//    public Result getGroupByOId(@RequestParam(value = "id" ) Integer id,
//                                @RequestBody ActiveQuery query) {
//        List<TuserGroup> result = new ArrayList<>();
//        List<TuserGroup> userGroups =  userGroupService.findByOperatorIdIn(Arrays.asList(id));
//        if(userGroups != null && !userGroups.isEmpty()){
//            for(TuserGroup group : userGroups){
//                Integer activeCount = saleRecordService.countSaleByGroupIds(Arrays.asList(group.getId()), 1, query.getStartTime(), query.getEndTime());
//                Integer unActiveCount = saleRecordService.countSaleByGroupIds(Arrays.asList(group.getId()), 0, query.getStartTime(), query.getEndTime());
//                group.setActiveCount(activeCount);
//                group.setUnActiveCount(unActiveCount);
//                result.add(group);
//            }
//            Map<String, Object> map = new HashMap<>();
//            map.put("groups", result);
//            return new Result(ResultConstant.SUCCESS, map);
//        }
//        return new Result(ResultConstant.SUCCESS, null);
//    }

}
