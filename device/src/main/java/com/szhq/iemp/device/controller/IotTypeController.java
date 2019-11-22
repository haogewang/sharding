package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TiotType;
import com.szhq.iemp.device.api.service.IotTypeService;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(description = "无线服务商模块")
@RestController
@RequestMapping("/iotType")
public class IotTypeController {
    private static final Logger logger = LoggerFactory.getLogger(IotTypeController.class);

    @Autowired
    private IotTypeService iotTypeService;

    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) OperatorQuery query) {
        logger.info("iot-type query:" + JSONObject.toJSONString(query));
        MyPage<TiotType> list = iotTypeService.findAllByCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("iotTypes", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "无线服务商添加", notes = "无线服务商添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Valid @RequestBody TiotType entity) {
        logger.info("add iotType entity:" + entity);
        TiotType device = iotTypeService.save(entity);
        if (device != null) {
            return new Result(ResultConstant.SUCCESS, device);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "无线服务商删除", notes = "无线服务商删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result delete(@RequestParam("id") Integer id) {
        logger.info("delete iotType id:" + id);
        Integer i = iotTypeService.deleteById(id);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, null);
    }


}
