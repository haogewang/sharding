package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceStoreHouse;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.service.DeviceStoreHouseService;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;
import com.szhq.iemp.device.api.vo.query.StorehouseQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库控制器
 * @author wanghao
 */
@Api(description = "仓库模块")
@RestController
@RequestMapping("/storehouse")
public class DeviceStoreHouseController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceStoreHouseController.class);

    @Autowired
    private DeviceStoreHouseService deviceStoreHouseService;
    @Autowired
    private OperatorService operatorService;

    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) StorehouseQuery query) {
        MyPage<TdeviceStoreHouse> list = deviceStoreHouseService.findAllByCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        List<TdeviceStoreHouse> storeHouseList = new ArrayList<>();
        for(TdeviceStoreHouse  storeHouse : list.getContent()){
            if(storeHouse.getOperatorId() != null){
                Toperator operator = operatorService.findById(storeHouse.getOperatorId());
                if(operator != null){
                    storeHouse.setOperatorName(operator.getName());
                }
            }
            storeHouseList.add(storeHouse);
        }
        result.put("storhouses", storeHouseList);
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "添加仓库", notes = "添加仓库")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody TdeviceStoreHouse entity) {
        logger.info("add deviceStoreHouse entity:" + entity);
        TdeviceStoreHouse deviceStoreHouse = deviceStoreHouseService.add(entity);
        if (deviceStoreHouse != null) {
            return new Result(ResultConstant.SUCCESS, deviceStoreHouse);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "根据名称查仓库是否存在", notes = "根据名称查仓库是否存在(true表示存在)")
    @RequestMapping(value = "/isNameExist", method = RequestMethod.GET)
    public Result isNameExist(@RequestParam("name") String name) {
        TdeviceStoreHouse storeHouse = deviceStoreHouseService.findByName(name);
        if(storeHouse != null) {
            return new Result(ResultConstant.SUCCESS, true);
        }
        return new Result(ResultConstant.SUCCESS, false);
    }

    @ApiOperation(value = "查找运营公司下所有仓库", notes = "根据运营公司Id查找运营公司下所有仓库")
    @RequestMapping(value = "/findAllStoresByOperatorId", method = RequestMethod.GET)
    public Result findAllStoresByOperatorId(@RequestParam("operatorId") Integer operatorId) {
        List<TdeviceStoreHouse> storeHouses = deviceStoreHouseService.findAllStoresByOperatorId(operatorId);
        return new Result(ResultConstant.SUCCESS, storeHouses);
    }

    @ApiOperation(value = "编辑仓库", notes = "编辑仓库")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result edit(@RequestBody TdeviceStoreHouse entity) {
        TdeviceStoreHouse deviceStoreHouse = deviceStoreHouseService.update(entity);
        if (deviceStoreHouse != null) {
            return new Result(ResultConstant.SUCCESS, deviceStoreHouse);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "删除仓库", notes = "删除仓库")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result delete(@RequestParam(value = "id") Integer id) {
        Integer count = deviceStoreHouseService.deleteById(id);
        if (count > 0) {
            return new Result(ResultConstant.SUCCESS, count);
        }
        return new Result(ResultConstant.FAILED, "");
    }

    @ApiOperation(value = "仓库设备激活数量统计", notes = "仓库设备激活数量统计")
    @RequestMapping(value = "/deviceActiveStatistic", method = RequestMethod.POST)
    public Result deviceStatistic(@RequestParam(value = "storehouseId",required = false) Integer id,
                                  @RequestBody(required = false) StorehouseQuery query) {
        List<ActiveDeviceCount> result = deviceStoreHouseService.deviceActiveStatistic(id, query);
        return new Result(ResultConstant.SUCCESS, result);
    }

}
