package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TdeviceManufactor;
import com.szhq.iemp.device.api.service.DeviceManufactorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "设备制造商模块")
@RestController
@RequestMapping("/manufactor")
public class DeviceManufactorController {
	private static final Logger logger = LoggerFactory.getLogger(DeviceManufactorController.class);
	
	@Autowired
	private DeviceManufactorService manufactorService;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加设备厂商", notes = "添加设备厂商")
	public Result add(@RequestBody TdeviceManufactor entity) {
		logger.info("add deviceManufacturer entity:" + entity);
		TdeviceManufactor device = manufactorService.save(entity);
		if(device != null) {
			return new Result(ResultConstant.SUCCESS, device);
		}
		return new Result(ResultConstant.FAILED, null);
	}

	@ApiOperation(value = "删除设备厂商", notes = "删除设备厂商")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result delete(@RequestParam(value = "id") Integer id) {
		logger.info("delete deviceManufacturer id:" + id);
		Integer j = manufactorService.deleteById(id);
		if(j > 0) {
			logger.info("delete manufacture success.id:" + id);
			return new Result(ResultConstant.SUCCESS, j);
		}
		return new Result(ResultConstant.FAILED, j);
	}
	
}
