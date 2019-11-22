package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TaddressRegion;
import com.szhq.iemp.device.api.service.AddressRegionService;
import com.szhq.iemp.device.api.vo.AdressRegionVo;
import com.szhq.iemp.device.api.vo.query.RegionQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "区域模块")
@RestController
@RequestMapping("/region")
public class AddressRegionController {
	private static final Logger logger = LoggerFactory.getLogger(AddressRegionController.class);

	@Autowired
	private AddressRegionService addressRegionService;

	@ApiOperation(value = "区域列表", notes = "区域列表")
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result list(
			@RequestParam(value = "offset") Integer offset,
			@RequestParam(value = "pagesize") Integer limit,
			@RequestParam(required = false, value = "sort") String sort,
			@RequestParam(required = false, value = "order") String order,
			@RequestBody(required = false) RegionQuery query) {
		logger.info("address-query:" + JSONObject.toJSONString(query));
		MyPage<TaddressRegion> list = addressRegionService.findByCretira(offset, limit, sort, order, query);
		Map<String, Object> result = new HashMap<>();
		result.put("regions", list.getContent());
		result.put("total", list.getTotal());
		return new Result(ResultConstant.SUCCESS, result);
	}

	@ApiOperation(value = "根据id获取某一区域下一级所有子区域", notes = "根据id获取某一区域所有子区域")
	@RequestMapping(value = "/getNexLevelChildrenById", method = RequestMethod.GET)
	public Result getAllChildren(@RequestParam(value = "id") Integer id) {
		List<AdressRegionVo> list = addressRegionService.findNexLevelAllChildrenById(id);
		return new Result(ResultConstant.SUCCESS, list);
	}
	
	@ApiOperation(value = "根据ids获取区域下一级所有子区域", notes = "根据ids获取区域所有子区域")
	@RequestMapping(value = "/getNexLevelChildrenByIds", method = RequestMethod.POST)
	public Result getAllChildrenByIds(@RequestBody List<Integer> ids) {
		Map<Integer, List<AdressRegionVo>> list = addressRegionService.getNexLevelAllChildrenByIds(ids);
		return new Result(ResultConstant.SUCCESS, list);
	}

	@ApiOperation(value = "获取安装点所有的市", notes = "获取安装点所有的市")
	@RequestMapping(value = "/getAllSiteRegions", method = RequestMethod.POST)
	public Result getAllSiteRegions() {
		List<TaddressRegion> result = addressRegionService.getAllSiteCities();
		return new Result(ResultConstant.SUCCESS, result);
	}

    @ApiOperation(value = "获取某市下已有安装点的所有区", notes = "获取某市下已有安装点的所有区")
    @RequestMapping(value = "/getAllRegionsByCityId", method = RequestMethod.GET)
    public Result getAllRegionsByCityId(@RequestParam(value = "cityId") Integer id) {
        List<TaddressRegion> result = addressRegionService.getAllRegionsByCityId(id);
        return new Result(ResultConstant.SUCCESS, result);
    }

	@ApiOperation(value = "删除区域redis缓存", notes = "删除区域redis缓存")
	@RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
	public Result deleteRedis() {
		Integer count = addressRegionService.deleteRegionRedisData();
		return new Result(ResultConstant.SUCCESS, count);
	}
	
}
