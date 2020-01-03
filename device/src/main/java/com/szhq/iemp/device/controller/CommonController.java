package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/common", description = "通用模块")
@RestController
@RequestMapping("/common")
public class CommonController {

    @ApiOperation(value = "查看堆栈", notes = "查看堆栈")
    @RequestMapping(value = "/getAllStackInfo", method = RequestMethod.GET)
    public Result getAllStackInfo() {
        return new Result(ResultConstant.SUCCESS, Thread.getAllStackTraces());
    }

}
