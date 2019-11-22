package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceDispatchHistory;
import com.szhq.iemp.device.api.service.DeviceDispacheHistoryService;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 分配记录
 * @author wanghao
 */
@Api(description = "分配记录模块")
@RestController
@RequestMapping("/dispacherlog")
public class DeviceDispacherLogController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceDispacherLogController.class);

    @Autowired
    private DeviceDispacheHistoryService dispacheHistoryService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "分配记录列表", notes = "分配记录列表")
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) DeviceQuery query) {
        logger.info("dispatch-log-query:" + JSONObject.toJSONString(query));
        MyPage<TdeviceDispatchHistory> list = dispacheHistoryService.findAllByCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("dispatchLogs", list.getContent());
        result.put("total", list.getTotal());
        result.put("currentPage", list.getPageNo());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/deleteDispatchLogRedis", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除redis缓存", notes = "删除redis缓存")
    public Result deleteRedisData() {
        Integer i = dispacheHistoryService.deleteDisPatchRedis();
        return new Result(ResultConstant.SUCCESS, i);
    }
}
