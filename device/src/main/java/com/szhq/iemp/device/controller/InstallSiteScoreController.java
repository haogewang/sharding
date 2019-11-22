package com.szhq.iemp.device.controller;

import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TinstallSiteScore;
import com.szhq.iemp.device.api.service.InstallSiteScoreService;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(description = "安装点评分模块")
@RestController
@Slf4j
@RequestMapping("/score")
public class InstallSiteScoreController {

	@Autowired
	private InstallSiteScoreService installSiteScoreService;

	@ApiOperation(value = "获取安装点及安装人员信息", notes = "获取安装点及安装人员信息")
	@RequestMapping(value = "/getSiteAndWorkerInfoByImei", method = RequestMethod.GET)
	public Result getInstallSiteAndWorkerInfo(@RequestParam(value = "imei") String imei) {
		InstallSiteAndWorker installSiteAndWorker = installSiteScoreService.getInstallSiteAndWorkerInfo(imei);
		return new Result(ResultConstant.SUCCESS, installSiteAndWorker);
	}

	@ApiOperation(value = "保存打分记录", notes = "保存打分记录")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Result save(@Valid @RequestBody TinstallSiteScore score, BindingResult result) {
		if(result.hasErrors()) {
			log.error("save score error." + result.getFieldError().getDefaultMessage());
			return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
		}
		Boolean isScore = installSiteScoreService.isScore(score.getImei());
		if(isScore){
			log.error("the imei has scored. imei:" + score.getImei());
			throw new NbiotException(500, "您已进行过评价");
		}
		Integer i = installSiteScoreService.save(score);
		return new Result(ResultConstant.SUCCESS, i);
	}

	@ApiOperation(value = "根据安装点Id获取指定安装点打分详情", notes = "根据安装点Id获取指定安装点打分详情")
	@RequestMapping(value = "/getDetailBySiteId", method = RequestMethod.GET)
	public Result getDetailByInstallSiteId(@RequestParam(value = "installSiteId") Integer installSiteId) {
		List<TinstallSiteScore> result = installSiteScoreService.getDetailByInstallSiteId(installSiteId);
		return new Result(ResultConstant.SUCCESS, result);
	}

	@ApiOperation(value = "根据安装人员Id获取打分详情", notes = "根据安装人员Id获取打分详情")
	@RequestMapping(value = "/getDetailByWorkerId", method = RequestMethod.GET)
	public Result getDetailByWorkerId(@RequestParam(value = "workerId") String workerId) {
		List<TinstallSiteScore> result = installSiteScoreService.getDetailByWorkerId(workerId);
		return new Result(ResultConstant.SUCCESS, result);
	}

	@ApiOperation(value = "根据安装点Id获取安装人员及平均分信息", notes = "根据安装点Id获取安装人员及平均分信息")
	@RequestMapping(value = "/getWorkerAvgScoreAndInfoBySiteId", method = RequestMethod.GET)
	public Result getWorkerAvgScoreAndInfoByInstallSiteId(@RequestParam(value = "installSiteId") Integer installSiteId) {
		List<InstallSiteAndWorker> result = installSiteScoreService.getWorkerAvgScoreAndInfoByInstallSiteId(installSiteId);
		return new Result(ResultConstant.SUCCESS, result);
	}


	@ApiOperation(value = "根据imei获取是否已评价", notes = "根据imei获取是否已评价(true:已评价)")
	@RequestMapping(value = "/isScore", method = RequestMethod.GET)
	public Result isScore(@RequestParam(value = "imei") String imei) {
		Boolean result = installSiteScoreService.isScore(imei);
		return new Result(ResultConstant.SUCCESS, result);
	}

	@ApiOperation(value = "获取指定安装点平均分", notes = "获取指定安装点平均分")
	@RequestMapping(value = "/getSiteAvgScore", method = RequestMethod.GET)
	public Result getInstallSiteAvgScore(@RequestParam(value = "installSiteId") Integer installSiteId) {
		Double score = installSiteScoreService.getInstallSiteAvgScoreByInstallId(installSiteId);
		return new Result(ResultConstant.SUCCESS, score);
	}

	@ApiOperation(value = "获取指定安装人员平均分", notes = "获取指定安装人员平均分")
	@RequestMapping(value = "/getWorkerAvgScore", method = RequestMethod.GET)
	public Result getInstallWorkerAvgScore(@RequestParam(value = "workerId") String workerId) {
		Double score = installSiteScoreService.getWorkerAvgScoreByWorkerId(workerId);
		return new Result(ResultConstant.SUCCESS, score);
	}
	
}
