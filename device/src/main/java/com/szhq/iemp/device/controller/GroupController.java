package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.constant.enums.exception.GroupExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.model.Tgroup;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.ElectrmobileService;
import com.szhq.iemp.device.api.service.GroupService;
import com.szhq.iemp.device.api.vo.UserAndElecInfo;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.device.api.vo.query.GroupQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(description = "分组模块")
@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private ElectrmobileService electrmobileService;

    @ApiOperation(value = "分组列表", notes = "分组列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) GroupQuery query) {
        log.info("group-query:" + JSONObject.toJSONString(query));
        List<Tgroup> results = new ArrayList<>();
        MyPage<Tgroup> list = groupService.findGroupByCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("groups", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据组id查找设备列表", notes = "根据组id查找设备列表")
    @RequestMapping(value = "/getDevicesByGroupId", method = RequestMethod.POST)
    public Result getDevicesByGroupId(@RequestParam(value = "offset") Integer offset,
                                      @RequestParam(value = "pagesize") Integer limit,
                                      @RequestBody GroupQuery query){
        if(query.getGroupId() == null){
            throw new NbiotException(500006, GroupExceptionEnum.E_0008.getMessage());
        }
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setGroupId(query.getGroupId());
        if(StringUtils.isNotEmpty(query.getImei())){
            deviceQuery.setImei(query.getImei());
        }
        MyPage<TdeviceInventory> devices = deviceInventoryService.findAllByCriteria(offset, limit, null, "desc", deviceQuery, false, false);
        return new Result(ResultConstant.SUCCESS, devices);
    }

    @ApiOperation(value = "根据组id查找车辆列表", notes = "根据组id查找车辆列表")
    @RequestMapping(value = "/getElecsByGroupId", method = RequestMethod.POST)
    public Result getElecsByGroupId(@RequestParam(value = "offset") Integer offset,
                                    @RequestParam(value = "pagesize") Integer limit,
                                    @RequestBody GroupQuery query){
        List<UserAndElecInfo> result = new ArrayList<>();
        if(query.getGroupId() == null){
            throw new NbiotException(500006, GroupExceptionEnum.E_0008.getMessage());
        }
        ElecmobileQuery elecmobileQuery = new ElecmobileQuery();
        elecmobileQuery.setGroupId(query.getGroupId());
        if(StringUtils.isNotEmpty(query.getPlateNo())){
            elecmobileQuery.setPlateNo(query.getPlateNo());
        }
        MyPage<Telectrmobile> electrmobiles = electrmobileService.findElecByCriteria(offset, limit, null, "desc", elecmobileQuery);
        if(electrmobiles != null && electrmobiles.getContent() != null && electrmobiles.getTotal() > 0){
            List<String> imeis = electrmobiles.getContent().stream().map(Telectrmobile::getImei).collect(Collectors.toList());
            result = groupService.getUserAndElecInfoByImeis(imeis);
            return new Result(ResultConstant.SUCCESS, result);
        }
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据id查找设备分组的所有子类", notes = "根据id查找设备分组的所有子类")
    @RequestMapping(value = "/getAllDeviceGroupsByPId", method = RequestMethod.GET)
    public Result getAllDeviceGroupByPId(@RequestParam(value = "parentId") Integer id) {
        List<Tgroup> groups = groupService.getAllDeviceGroupChildrenById(id);
        return new Result(ResultConstant.SUCCESS, groups);
    }

    @ApiOperation(value = "根据id查找车辆分组的所有子类", notes = "根据id查找车辆分组的所有子类")
    @RequestMapping(value = "/getAllElecGroupsByPId", method = RequestMethod.GET)
    public Result getAllElecGroupsByPId(@RequestParam(value = "parentId") Integer id) {
        List<Tgroup> groups = groupService.getAllElecGroupChildrenById(id);
        return new Result(ResultConstant.SUCCESS, groups);
    }

    @ApiOperation(value = "根据id查找车辆分组的所下级子类", notes = "根据id查找车辆分组的下级子类")
    @RequestMapping(value = "/getNextElecGroupsByPId", method = RequestMethod.GET)
    public Result getNextElecGroupsByPId(@RequestParam(value = "parentId") Integer id) {
        List<Tgroup> groups = groupService.getNextElecGroupById(id);
        return new Result(ResultConstant.SUCCESS, groups);
    }

    @ApiOperation(value = "根据id查找设备分组的所下级子类", notes = "根据id查找设备分组的下级子类")
    @RequestMapping(value = "/getNextDeviceGroupsByPId", method = RequestMethod.GET)
    public Result getNextDeviceGroupsByPId(@RequestParam(value = "parentId") Integer id) {
        List<Tgroup> groups = groupService.getNextDeviceGroupById(id);
        return new Result(ResultConstant.SUCCESS, groups);
    }

    @ApiOperation(value = "添加组", notes = "添加组")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Valid @RequestBody Tgroup entity, BindingResult result) {
        log.info("add group entity:" + JSONObject.toJSONString(entity));
        if (result.hasErrors() && result.getFieldError() != null) {
            log.error("add group error." + result.getFieldError().getDefaultMessage());
            return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
        }
        Tgroup group = groupService.save(entity);
        if(group != null) {
            return new Result(ResultConstant.SUCCESS, group);
        }
        return new Result(ResultConstant.FAILED, GroupExceptionEnum.E_0005.getMessage());
    }

    @ApiOperation(value = "给设备分组", notes = "给设备分组")
    @RequestMapping(value = "/dispatchToDeviceGroup", method = RequestMethod.POST)
    public Result dispatchDeviceToGroup(@RequestBody List<String> imeis,
                                        @RequestParam("groupId") Integer groupId) {
        Integer count = groupService.dispatchToDeviceGroup(imeis, groupId);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "给车辆分组", notes = "给车辆分组")
    @RequestMapping(value = "/dispatchToElecGroup", method = RequestMethod.POST)
    public Result dispatchToElecGroup(@RequestBody List<String> imeis,
                                        @RequestParam("groupId") Integer groupId) {
        Integer count = groupService.dispatchToElecGroup(imeis, groupId);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "移除设备分组", notes = "移除设备分组")
    @RequestMapping(value = "/removeDeviceGroup", method = RequestMethod.POST)
    public Result removeDeviceGroup(@RequestBody List<String> imeis) {
        Integer count = groupService.removeDeviceGroup(imeis);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "移除车辆分组", notes = "移除车辆分组")
    @RequestMapping(value = "/removeElecGroup", method = RequestMethod.POST)
    public Result removeElecGroup(@RequestBody List<String> imeis) {
        Integer count = groupService.removeElecGroup(imeis);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "删除设备分组", notes = "删除设备分组")
    @RequestMapping(value = "/deleteDeviceGroup", method = RequestMethod.DELETE)
    public Result deleteDeviceGroup(@RequestParam(value = "id") Integer id) {
        log.info("delete group id:" + id);
        Integer i = groupService.deleteDeviceGroupById(id);
        if(i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, GroupExceptionEnum.E_0006.getMessage());
    }

    @ApiOperation(value = "删除车辆分组", notes = "删除车辆分组")
    @RequestMapping(value = "/deleteElecGroup", method = RequestMethod.DELETE)
    public Result deleteElecGroup(@RequestParam(value = "id") Integer id) {
        log.info("delete group id:" + id);
        Integer i = groupService.deleteElecGroupById(id);
        if(i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, GroupExceptionEnum.E_0006.getMessage());
    }
}
