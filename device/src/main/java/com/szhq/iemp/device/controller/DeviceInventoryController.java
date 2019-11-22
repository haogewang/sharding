package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.vo.*;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Api(description = "设备模块")
@Slf4j
@RestController
@RequestMapping("/deviceInventory")
public class DeviceInventoryController {

    @Autowired
    private DeviceInventoryService deviceInventoryService;

    @ApiOperation(value = "设备列表", notes = "设备列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestParam(required = false, value = "dispacher") Boolean isDispacher,
                         @RequestParam(required = false, value = "outStore") Boolean isOutStore,
                         @RequestBody(required = false) DeviceQuery query) {
        log.info("device-query:" + JSONObject.toJSONString(query));
        MyPage<TdeviceInventory> list = deviceInventoryService.findAllByCriteria(offset, limit, sort, order, query, isDispacher, isOutStore);
        Map<String, Object> result = new HashMap<>();
        if(query != null && query.getIsInstallSite()){
            List<TdeviceInventory> devices = new ArrayList<>();
            for(TdeviceInventory device : list.getContent()){
                RegisterVo registerVo = deviceInventoryService.getInstalledWorkerByImei(device.getImei());
                device.setRegisterVo(registerVo);
                devices.add(device);
            }
            result.put("devices", devices);
            result.put("total", list.getTotal());
            result.put("currentPage", list.getPageNo());
            return new Result(ResultConstant.SUCCESS, result);
        }
        result.put("devices", list.getContent());
        result.put("total", list.getTotal());
        result.put("currentPage", list.getPageNo());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "按箱号展示入库列表", notes = "入库按箱号展示列表")
    @RequestMapping(value = "/getBoxNumbersOfPutStorage", method = RequestMethod.POST)
    public Result getBoxNumbersOfPutStorage(@RequestParam(value = "offset") Integer offset,
                                            @RequestParam(value = "pagesize") Integer limit,
                                            @RequestBody DeviceQuery query) {
        List<TdeviceInventory> list = deviceInventoryService.getBoxNumbersOfPutStorage(offset, limit, query);
        return new Result(ResultConstant.SUCCESS, list);
    }
    /**
     *{"devList":
     * [{"devtype":"2","iccid":"2","devname":"2","imei":"12345","imsi":"2ewew","iotDeviceId":"ee2","manufactorId":1,
     *   "operatorId":1,"regionId":110000,"storehouseId":1,"swVersion":"ee2e2","iotTypeId":1,"modelNo":"302"
     * }]}
     */
    @ApiOperation(value = "设备导入", notes = "设备导入")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDeviceInventory(@Valid @RequestBody HashMap<String, List<TdeviceInventory>> deviceListMap, BindingResult result) {
        if (result.hasErrors()) {
            log.error("import error.", result.getFieldError().getDefaultMessage());
            return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
        }
        List<TdeviceInventory> deviceInventoryList = deviceListMap.get("devList");
        if (deviceInventoryList.size() > 1200) {
            return new Result(ResultConstant.FAILED, "deviceList is too large");
        }
        Integer i = deviceInventoryService.importDevice(deviceInventoryList);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "根据箱号设备入库", notes = "根据箱号设备入库")
    @RequestMapping(value = "/putInStorageByBoxNumbers", method = RequestMethod.POST)
    public Result putinStorage(
            @RequestBody List<String> boxNumbers,
            @RequestParam(value = "deviceStorehouseId") Integer storehouseId, HttpServletRequest request) {
        Integer i = deviceInventoryService.putinStorageByBoxNumbers(boxNumbers, storehouseId, request);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "获取可分配的箱号", notes = "获取可分配的箱号")
    @RequestMapping(value = "/getDispatchBoxNumbers", method = RequestMethod.POST)
    public Result getDispatchBoxNumbers(@RequestBody(required = false) DeviceQuery query) {
        log.info("getDispatchBoxNumbers query:" + JSONObject.toJSONString(query));
        Map<String, List<DeviceOfBox>> list = deviceInventoryService.getDispatchBoxNumbers(query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "根据imeis分配设备", notes = "根据imeis分配设备")
    @RequestMapping(value = "/dispatchByImeis", method = RequestMethod.POST)
    public Result dispatchByImeis(
                                  @RequestBody List<String> imeis,
                                  @RequestParam(value = "installSiteId") Integer installSiteId) {
        Integer count = deviceInventoryService.dispatchByImeis(imeis, installSiteId);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "根据箱号分配设备", notes = "根据箱号分配设备")
    @RequestMapping(value = "/dispatchByBoxNumbers", method = RequestMethod.POST)
    public Result dispatchByBoxNumber(@RequestBody List<String> boxNumbers,
                                      @RequestParam(value = "installSiteId") Integer installSiteId) {
        Integer count = deviceInventoryService.dispatchByBoxNumber(boxNumbers, installSiteId);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "根据组Id查找所有imeis", notes = "根据imei查找运营公司")
    @RequestMapping(value = "/getImeisByGroupId", method = RequestMethod.GET)
    public Result getImeisByGroupId(@RequestParam("groupId") Integer groupId) {
        List<String> imeis = deviceInventoryService.getImeisByGroupId(groupId);
        return new Result(ResultConstant.SUCCESS, imeis);
    }

    @ApiOperation(value = "根据入库时间获取箱号及其设备数", notes = "根据入库时间获取箱号及其设备数")
    @RequestMapping(value = "/getBoxNumberByPutStorageTime", method = RequestMethod.POST)
    public Result getBoxNumberByPutStorageTime(@RequestBody(required = false) DeviceQuery query) {
        Map<String, List<DeviceOfBox>> list = deviceInventoryService.getBoxNumberByPutStorageTime(query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "根据箱号查找所有设备", notes = "根据箱号查找所有设备")
    @RequestMapping(value = "/getDevicesByBoxNumber", method = RequestMethod.GET)
    public Result getDevicesByBoxNumber(@RequestParam(value = "boxNumber") String boxNumber) {
        List<TdeviceInventory> list = deviceInventoryService.getDevicesByBoxNumber(boxNumber);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "根据isp统计未分配设备", notes = "根据isp统计未分配设备")
    @RequestMapping(value = "/getCountOfIsp", method = RequestMethod.POST)
    public Result getUnDispacheDeviceCountOfIsp(@RequestBody(required = false) DeviceQuery query) {
        log.info("unDispacheDeviceCount query:" + JSONObject.toJSONString(query));
        List<UnDispacheDeviceCount> list = deviceInventoryService.getUndispacheDeviceCountOfIsp(query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "验证入库箱子状态", notes = "验证入库箱子状态")
    @RequestMapping(value = "/validPutStorageByBoxNumber", method = RequestMethod.GET)
    public Result validPutStorageByBoxNumber(@RequestParam("boxNumber") String boxNumber) {
        DeviceOfBox boxnumber = deviceInventoryService.validPutStorageByBoxNumber(boxNumber);
        return new Result(ResultConstant.SUCCESS, boxnumber);
    }

    @ApiOperation(value = "验证出库imei状态", notes = "验证出库imei状态")
    @RequestMapping(value = "/validImeiInfo", method = RequestMethod.GET)
    public Result validImeiInfo(@RequestParam("imei") String imei) {
        TdeviceInventory device = deviceInventoryService.validImeiInfo(imei);
        return new Result(ResultConstant.SUCCESS, device);
    }

    @ApiOperation(value = "验证退库箱子状态", notes = "验证退库箱子状态")
    @RequestMapping(value = "/validBackoffInfoByBoxNumbers", method = RequestMethod.GET)
    public Result validBackoffInfoByBoxNumbers(@RequestBody List<String> boxNumbers) {
        List<String> boxnumbers = deviceInventoryService.validBackoffInfoByBoxNumbers(boxNumbers);
        return new Result(ResultConstant.SUCCESS, boxnumbers);
    }

    @ApiOperation(value = "验证退库设备状态", notes = "验证退库imei状态")
    @RequestMapping(value = "/validBackoffInfoByImei", method = RequestMethod.GET)
    public Result validBackoffInfoByImeis(@RequestParam("imei") String imei) {
        TdeviceInventory device = deviceInventoryService.validBackoffInfoByImei(imei);
        return new Result(ResultConstant.SUCCESS, device);
    }

    @ApiOperation(value = "验证退货设备状态", notes = "验证退货设备状态")
    @RequestMapping(value = "/validReturnOffInfoByImei", method = RequestMethod.GET)
    public Result validReturnOffInfoByBoxNumbers(@RequestParam("imei") String imei) {
        TdeviceInventory device = deviceInventoryService.validReturnOffInfoByImei(imei);
        return new Result(ResultConstant.SUCCESS, device);
    }

    @ApiOperation(value = "获取可退库的箱号", notes = "获取可退库的箱号")
    @RequestMapping(value = "/getBackOffBoxNumbers", method = RequestMethod.POST)
    public Result getBackOffBoxNumbers(@RequestBody(required = false) DeviceQuery query) {
        log.info("getBackOffBoxNumbers-query:" + JSONObject.toJSONString(query));
        Map<String, List<DeviceOfBox>> list = deviceInventoryService.getBackOffBoxNumbers(query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "根据箱号退库", notes = "根据箱号退库")
    @RequestMapping(value = "/backByBoxNumbers", method = RequestMethod.POST)
    public Result backByBoxNumber(@RequestBody List<String> boxNumbers) {
        Integer count = deviceInventoryService.backOffByBoxNumbers(boxNumbers);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "根据设备号退库", notes = "根据设备号退库")
    @RequestMapping(value = "/backByImeis", method = RequestMethod.POST)
    public Result backByImei(@RequestBody List<String> imeis) {
        Integer count = deviceInventoryService.backOffByImeis(imeis);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "根据imei退货", notes = "根据imei退货")
    @RequestMapping(value = "/returnByImeis", method = RequestMethod.POST)
    public Result returnByImeis(@RequestBody List<String> imeis) {
        Integer count = deviceInventoryService.returnOffDeviceByImeis(imeis);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "入库统计", notes = "入库统计")
    @RequestMapping(value = "/putStorageStatistic", method = RequestMethod.POST)
    public Result putStorageStatistic(@RequestParam(value = "offset") Integer offset,
                                      @RequestParam(value = "pagesize") Integer limit,
                                      @RequestBody(required = false) DeviceQuery query) {
        if (query == null) query = new DeviceQuery();
        log.info("putStorageStatistic-query:" + JSONObject.toJSONString(query));
        List<PutStorageCount> list = deviceInventoryService.putStorageStatistic(offset, limit, query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "分配统计", notes = "分配统计")
    @RequestMapping(value = "/dispatchStatistic", method = RequestMethod.POST)
    public Result dispatchStatistic(@RequestParam(value = "offset") Integer offset,
                                    @RequestParam(value = "pagesize") Integer limit,
                                    @RequestBody(required = false) DeviceQuery query) {
        log.info("dispatchStatic query:" + JSONObject.toJSONString(query));
        List<DispachCount> list = deviceInventoryService.dispatchStatistic(offset, limit, query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "退库统计", notes = "退库统计")
    @RequestMapping(value = "/backOffStatistic", method = RequestMethod.POST)
    public Result backOffStatistic(@RequestParam(value = "offset") Integer offset,
                                   @RequestParam(value = "pagesize") Integer limit,
                                   @RequestBody(required = false) DeviceQuery query) {
        log.info("backOffStatistic-query:" + JSONObject.toJSONString(query));
        List<DispachCount> list = deviceInventoryService.backOffStatistic(offset, limit, query);
        return new Result(ResultConstant.SUCCESS, list);
    }

    @ApiOperation(value = "删除redis缓存", notes = "删除redis缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis() {
        Integer i = deviceInventoryService.deleteDeviceRedisData();
        return new Result(ResultConstant.SUCCESS, i);
    }



}
