package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceDefectiveInventory;
import com.szhq.iemp.device.api.service.DeviceDefectiveInventoryService;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不良品库
 * @author wanghao
 */
@Api(description = "不良品库模块")
@RestController
@RequestMapping("/defective")
public class DeviceDefectiveController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceDefectiveController.class);

    @Autowired
    private DeviceDefectiveInventoryService defectiveInventoryService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;

    @ApiOperation(value = "不良品库列表", notes = "不良品库列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result list(
                    @RequestParam(value = "offset") Integer offset,
                    @RequestParam(value = "pagesize") Integer limit,
                    @RequestParam(required = false, value = "sort") String sort,
                    @RequestParam(required = false, value = "order") String order,
                    @RequestBody(required = false) DeviceQuery query) {
        logger.info("defective-query:" + JSONObject.toJSONString(query));
        MyPage<TdeviceDefectiveInventory> list = defectiveInventoryService.findByCretira(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("defectives", list.getContent());
        result.put("total", list.getTotal());
        result.put("currentPage", list.getPageNo());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "不良品转良品", notes = "不良品转良品")
    @RequestMapping(value = "/defectiveToNormalByImeis", method = RequestMethod.POST)
    public Result defectiveToNormalByImeis(@RequestBody List<String> imeis) {
        Integer i = deviceInventoryService.defectiveToNormalByImeis(imeis);
        return new Result(ResultConstant.SUCCESS, i);
    }

}
