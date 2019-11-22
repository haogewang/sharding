package com.szhq.iemp.reservation.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.constant.enums.exception.UserExceptionEnum;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.UserService;
import com.szhq.iemp.reservation.util.DecyptTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "用户模块")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private ElectrmobileService electrombileService;

	@ApiOperation(value = "根据用户账号获取用户信息", notes = "根据用户账号获取用户信息")
	@RequestMapping(value = "/getUserInfoByPhone", method = RequestMethod.GET)
	public Result edit(@RequestParam("phone") String phone) {
		Tuser user = userService.findByPhone(phone);
		if(user != null) {
			return new Result(ResultConstant.SUCCESS, user);
		}
		return new Result(ResultConstant.FAILED, null);
	}

	@ApiOperation(value = "根据用户Id获取用户所有电动车", notes = "根据用户Id获取用户所有电动车")
	@RequestMapping(value = "/getAllElecByUserId", method = RequestMethod.GET)
	public Result getAllElecByUserId(@RequestParam("userId") String userId,
									 @RequestParam(value = "type", required = false, defaultValue = "W302") String type,
									 @RequestParam(value = "isApp", defaultValue = "false") Boolean isApp,
									 HttpServletRequest request) {
		List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
		List<Telectrmobile> list = electrombileService.findAllElecByUserId(userId, type, operatorIds, isApp);
		return new Result(ResultConstant.SUCCESS, list);
	}

	@ApiOperation(value = "修改用户信息", notes = "修改用户信息")
	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public Result updateUserInfo(@RequestBody Tuser user) {
		userService.updateUser(user);
		return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
	}

}
