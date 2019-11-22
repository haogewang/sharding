package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;
import com.szhq.iemp.device.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "运营公司模块")
@Slf4j
@RestController
@RequestMapping("/operator")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "运营公司列表", notes = "运营公司列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) OperatorQuery query) {
        log.info("operator-query:" + JSONObject.toJSONString(query));
        Map<String, Object> result = new HashMap<>();
        MyPage<Toperator> list = operatorService.findAllByCriteria(offset, limit, sort, order, query);

        result.put("operators", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "通过id查找详情", notes = "通过id查找详情")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public Result getById(@RequestParam(value = "id") Integer id) {
        Toperator operator = operatorService.findById(id);
        return new Result(ResultConstant.SUCCESS, operator);
    }

    @ApiOperation(value = "查找需要入库的运营公司", notes = "查找需要入库的运营公司")
    @RequestMapping(value = "/findNeedPutStorage", method = RequestMethod.GET)
    public Result findNeedPutStorage() {
        List<Toperator> operators = operatorService.findNeedPutStorageOperators();
        return new Result(ResultConstant.SUCCESS, operators);
    }

    @ApiOperation(value = "根据名称查运营公司是否存在", notes = "根据名称查运营公司是否存在(true表示存在)")
    @RequestMapping(value = "/isNameExist", method = RequestMethod.GET)
    public Result isNameExist(@RequestParam("name") String name) {
        Toperator operator = operatorService.findByName(name);
        if(operator != null) {
            return new Result(ResultConstant.SUCCESS, true);
        }
        return new Result(ResultConstant.SUCCESS, false);
    }

    @ApiOperation(value = "运营公司添加", notes = "运营公司添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Toperator entity) {
        log.info("add operator entity:" + JSONObject.toJSONString(entity));
        Toperator operator = operatorService.add(entity);
        if(operator != null) {
            return new Result(ResultConstant.SUCCESS, operator);
        }
        return new Result(ResultConstant.FAILED, OperatorExceptionEnum.E_0007.getMessage());
    }

    @ApiOperation(value = "根据id查所有子运营公司Id", notes = "根据id查所有子运营公司Id")
    @RequestMapping(value = "/getAllChildrenById", method = RequestMethod.GET)
    public Result getAllChildrenById(@RequestParam(value = "id") Integer id) {
        List<Integer> lists = operatorService.findAllChildIds(id);
        return new Result(ResultConstant.SUCCESS, lists);
    }

    @ApiOperation(value = "根据id查所有子运营公司", notes = "根据id查所有子运营公司")
    @RequestMapping(value = "/getAllChildrenInfoById", method = RequestMethod.GET)
    public Result getAllChildrenInfoById(@RequestParam(value = "id") Integer id) {
        List<Toperator> operators = operatorService.findAllChildrenInfo(id);
        return new Result(ResultConstant.SUCCESS, operators);
    }

    @ApiOperation(value = "运营公司修改", notes = "运营公司修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody Toperator entity) {
        log.info("update operator entity:" + JSONObject.toJSONString(entity));
        Toperator operator = operatorService.update(entity);
        if(operator != null) {
            return new Result(ResultConstant.SUCCESS, operator);
        }
        return new Result(ResultConstant.FAILED, OperatorExceptionEnum.E_0008.getMessage());
    }

    @ApiOperation(value = "运营公司删除", notes = "运营公司删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result delete(@RequestParam(value = "id") Integer id) {
        log.info("delete operator id:" + id);
        Integer i = operatorService.delete(id);
        if(i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, i);
    }

    @ApiOperation(value = "往redis补充未添加imei对应的运营公司信息", notes = "往redis补充imei对应的运营公司信息")
    @RequestMapping(value = "/putImeiOperatorToRedis", method = RequestMethod.POST)
    public Result putImeiOperator() {
        int count = 0;
        Map<String, Integer> map = deviceInventoryService.findOperatorIdAndImeiRelations();
        if(map != null && map.size() > 0) {
            for (String imei : map.keySet()) {
                if(StringUtils.isEmpty((String)redisUtil.get(CommonConstant.OPERATOR_IMEI_KEY + imei))) {
                    redisUtil.set(CommonConstant.OPERATOR_IMEI_KEY + imei, String.valueOf(map.get(imei)));
                    count++;
                }
            }
        }
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "删除运营公司缓存", notes = "删除运营公司缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis(@RequestParam(value = "id") Integer id) {
        Integer i = operatorService.deleteRedisData(id);
        if(i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, i);
    }

}
