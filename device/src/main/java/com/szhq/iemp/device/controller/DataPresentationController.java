package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.util.ListUtils;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.InstallSiteService;
import com.szhq.iemp.device.api.vo.DeviceCount;
import com.szhq.iemp.device.api.vo.DispachCount;
import com.szhq.iemp.device.api.vo.InstallSiteDeviceCount;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.InstallSiteQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据展示页面接口
 * @author wanghao
 */
@Api(description = "数据展示模块")
@RestController
@RequestMapping("/data-presentation")
@Slf4j
public class DataPresentationController {

    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private InstallSiteService installSiteService;


    @ApiOperation(value = "安装信息", notes = "包括今日安装数、历史安装数、安装异常等")
    @RequestMapping(value = "/installedInfo", method = RequestMethod.POST)
    public Result installedInfo(@RequestBody(required = false) DeviceQuery query) {
        log.info("installedInfo query:" + JSONObject.toJSONString(query));
        List<DispachCount> historyInstalledUnormalCount = deviceInventoryService.countHistoryInstalledUnormalCount(query);
//		List<DispachCount> todayInstalledCount = deviceInventoryService.countTodayInstalledEquip();
//		List<DispachCount> historyInstalledCount = deviceInventoryService.countHistoryInstalledEquip();
//		List<DispachCount> historyInstalledOnlineCount = deviceInventoryService.countHistoryInstalledOnlineCount();
//		double rate = 0;
//		if(historyInstalledCount != null && historyInstalledCount.get(0) != null && historyInstalledCount.get(0).getTotalCount() != 0) {
//			if(historyInstalledOnlineCount != null && historyInstalledOnlineCount.get(0) != null) {
//				rate = new BigDecimal((float)historyInstalledOnlineCount.get(0).getTotalCount()/historyInstalledCount.get(0).getTotalCount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//			}
//		}
        Map<String, Object> result = new HashMap<>();
//		result.put("todayInstalledCount", todayInstalledCount);
//		result.put("historyInstalledCount", historyInstalledCount);
//		result.put("historyInstalledOnlineCount", historyInstalledOnlineCount);
        result.put("historyInstalledUnormalCount", historyInstalledUnormalCount);
//		result.put("onlineRate", rate);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "历史设备安装统计", notes = "历史设备安装统计")
    @RequestMapping(value = "/historyInstalledStatistics", method = RequestMethod.POST)
    public Result installedStatistics(@RequestParam("offset") Integer offset,
                                      @RequestBody(required = false) DeviceQuery query) {
        log.info("historyInstalledStatistics query:" + JSONObject.toJSONString(query));
        List<DeviceCount> counts = deviceInventoryService.countHistoryInstalledByOffset(offset, query);
        String date = TimeStampUtil.formatDate(new Date(), null);
        JSONObject json = new JSONObject();
        json.put("today", date);
        json.put("counts", counts);
        return new Result(ResultConstant.SUCCESS, json);
    }

    @ApiOperation(value = "设备上线率排名、备案统计通用接口", notes = "设备上线率排名、备案统计通用接口")
    @RequestMapping(value = "/countByCondition", method = RequestMethod.POST)
    public Result countByCondition(@RequestParam(required = false, value = "onLineRate") Double onLineRate,
                                   @RequestParam(value = "offset") Integer offset,
                                   @RequestParam(value = "limit") Integer limit,
                                   @RequestBody(required = false) InstallSiteQuery query) {
        log.info("register-online-statistic query:" + JSONObject.toJSONString(query));
        List<InstallSiteDeviceCount> filterCounts = new ArrayList<InstallSiteDeviceCount>();
        List<InstallSiteDeviceCount> lists = installSiteService.countByCondation(query);
        String[] sortNameArr = {"totalOnRate", "totalInstalledCount"};
        boolean[] isAscArr = {false, false};
        if (lists != null && lists.size() > 0) {
            if (onLineRate != null) {
                for (InstallSiteDeviceCount count : lists) {
                    if (onLineRate >= count.getTotalOnRate()) {
                        filterCounts.add(count);
                    }
                }
                ListUtils.sort(filterCounts, sortNameArr, isAscArr);
                List<InstallSiteDeviceCount> subFilterCounts = pageList(offset, limit, filterCounts);
                return new Result(ResultConstant.SUCCESS, subFilterCounts);
            }
            ListUtils.sort(lists, sortNameArr, isAscArr);
            List<InstallSiteDeviceCount> subCounts = pageList(offset, limit, lists);
            return new Result(ResultConstant.SUCCESS, subCounts);
        }
        return new Result(ResultConstant.SUCCESS, null);
    }

    @ApiOperation(value = "安装点排名", notes = "安装点排名")
    @RequestMapping(value = "/installSiteOrder", method = RequestMethod.POST)
    public Result installSiteOrder(@RequestParam("offset") Integer offset,
                                   @RequestBody(required = false) InstallSiteQuery query) {
        log.info("installSiteOrder query:" + JSONObject.toJSONString(query));
        JSONObject json = new JSONObject();
        List<InstallSiteDeviceCount> counts = installSiteService.installSiteOrder(offset, query);
        String date = TimeStampUtil.formatDate(new Date(), null);
        json.put("today", date);
        json.put("counts", counts);
        return new Result(ResultConstant.SUCCESS, json);
    }

    private List<InstallSiteDeviceCount> pageList(Integer offset, Integer limit, List<InstallSiteDeviceCount> counts) {
        if (counts.size() > 0) {
            int subCount = counts.size();
            int subPageTotal = (subCount / limit) + ((subCount % limit > 0) ? 1 : 0);
            int len = subPageTotal - 1;
            int toIndex = ((offset == len) ? subCount : ((offset + 1) * limit));
            List<InstallSiteDeviceCount> subDataList = null;
            int fromIndex = offset * limit;
            subDataList = counts.subList(fromIndex, toIndex);
            return subDataList;
        }
        return null;
    }

}
