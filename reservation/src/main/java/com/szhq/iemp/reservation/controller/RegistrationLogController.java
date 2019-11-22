package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.TregistrationLog;
import com.szhq.iemp.reservation.api.service.RegistrationLogService;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 备案记录
 * @author wanghao
 */
@Api(description = "备案记录模块")
@RestController
@RequestMapping("/registrationLog")
public class RegistrationLogController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationLogController.class);

    @Autowired
    private RegistrationLogService registrationLogService;

    @ApiOperation(value = "备案记录列表", notes = "备案记录列表")
    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public Result log(
                    @RequestParam(value = "offset") Integer offset,
                    @RequestParam(value = "pagesize") Integer limit,
                    @RequestParam(required = false, value = "sort") String sort,
                    @RequestParam(required = false, value = "order") String order,
                    @RequestBody(required = false) RegisterQuery query) {
        logger.debug("register-log-query:" + JSONObject.toJSONString(query));
        Page<TregistrationLog> list = registrationLogService.findRegistrationCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("reservations", list.getContent());
        result.put("total", list.getTotalElements());
        return new Result(ResultConstant.SUCCESS, result);
    }
}
