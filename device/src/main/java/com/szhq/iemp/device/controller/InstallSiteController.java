package com.szhq.iemp.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.device.api.model.TinstallSite;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.ElectrmobileService;
import com.szhq.iemp.device.api.service.InstallSiteService;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;
import com.szhq.iemp.device.api.vo.InstallSiteDeviceCount;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.InstallSiteQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安装点控制器
 */
@Api(description = "安装点模块")
@RestController
@RequestMapping("/site")
public class InstallSiteController {
    private static final Logger logger = LoggerFactory.getLogger(InstallSiteController.class);

    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private InstallSiteService installSiteService;

    @ApiOperation(value = "安装点列表及模糊查询", notes = "安装点列表及模糊查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) InstallSiteQuery query) {
        logger.info("installSite query:" + JSONObject.toJSONString(query));
        MyPage<TinstallSite> list = installSiteService.findAllByCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        List<TinstallSite> sites = new ArrayList<>();
        if(query != null && query.getIsStatistics()){
            for (TinstallSite site : list.getContent()) {
                Integer total = installSiteService.getTotalDeviceBySiteId(site.getInstallSiteId());
                Integer equip = installSiteService.getEquipDeviceBySiteId(site.getInstallSiteId());
                Integer online = installSiteService.getOnlineEquipDeviceBySiteId(site.getInstallSiteId());
                Integer today = installSiteService.getTodayInstalledCountBySiteId(site.getInstallSiteId());
                Integer todayOnline = installSiteService.getTodayOnlineCountBySiteId(site.getInstallSiteId());
                Double score = installSiteService.getScoreBySiteId(site.getInstallSiteId());
                site.setSiteTotal(total);
                site.setScore(score);
                site.setSiteEquip(equip);
                site.setTodayInstalledCount(today);
                site.setTodayOnlineCount(todayOnline);
                site.setSiteOnlineEquip(online);
                sites.add(site);
            }
        }else{
            sites.addAll(list.getContent());
        }
        result.put("sites", sites);
        result.put("total", list.getTotal());
        result.put("currentPage", list.getPageNo());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "全部设备、已安装设备、未安装设备总数", notes = "全部设备、已安装设备、未安装设备总数")
    @RequestMapping(value = "/countAll", method = RequestMethod.POST)
    public Result countAllDevice(@RequestBody(required = false) DeviceQuery deviceQuery) {
        logger.info("installSite-countAll-query:" + JSONObject.toJSONString(deviceQuery));
        //已安装设备
        Long installedCount = electrombileService.countByCriteria(deviceQuery);
        //全部设备
        if (deviceQuery == null) {
            deviceQuery = new DeviceQuery();
            deviceQuery.setIsDispache(true);
        }
        Long dispacheCount = deviceInventoryService.countByCriteria(deviceQuery);
        Long unUsedCount = dispacheCount - installedCount;
        JSONObject json = new JSONObject();
        json.put("allCount", dispacheCount);
        json.put("installedCount", installedCount);
        json.put("unInstalledCount", unUsedCount);
        return new Result(ResultConstant.SUCCESS, json);
    }

    @ApiOperation(value = "根据时间偏移计算某个安装点安装数/上线率（过去一周、月）", notes = "根据时间偏移计算某个安装点安装数/上线率（过去一周、月）")
    @RequestMapping(value = {"/countHistoryEquipByOffset", "/countHistoryOnLineByOffset"}, method = RequestMethod.GET)
    public Result countHistoryEquipByOffset(@RequestParam(value = "installSiteId") Integer installSiteId,
                                            @RequestParam(value = "offset") Integer offset) {
        List<InstallSiteDeviceCount> counts = installSiteService.countByOffsetAndInstallSiteId(installSiteId, offset);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        JSONObject json = new JSONObject();
        json.put("today", date);
        json.put("historyCounts", counts);
        return new Result(ResultConstant.SUCCESS, json);
    }

    @ApiOperation(value = "安装点添加", notes = "安装点添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Valid @RequestBody TinstallSite entity, BindingResult result) {
        logger.info("add site entity:" + entity);
        if (result.hasErrors()) {
            logger.error("add site error." + result.getFieldError().getDefaultMessage());
            return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
        }
        TinstallSite site = installSiteService.add(entity);
        if (site != null) {
            return new Result(ResultConstant.SUCCESS, site);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "安装点修改", notes = "安装点修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody TinstallSite entity) {
        logger.info("update installSite:" + JSONObject.toJSONString(entity));
        Integer i = installSiteService.update(entity);
        if (i > 0) {
            logger.debug("update site success.");
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, i);
    }

    @ApiOperation(value = "安装点删除", notes = "安装点删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result update(@RequestParam("id") Integer id) {
        Integer i = installSiteService.deleteById(id);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, i);
    }

    @ApiOperation(value = "根据id查找安装点", notes = "根据id查找安装点")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public Result findById(@RequestParam("id") Integer id) {
        TinstallSite installSite = installSiteService.findById(id);
        return new Result(ResultConstant.SUCCESS, installSite);
    }

    @ApiOperation(value = "根据名称查找安装点", notes = "根据名称查找安装点")
    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    public Result findByName(@RequestParam("name") String name) {
        TinstallSite installSite = installSiteService.findByName(name);
        if (installSite != null) {
            return new Result(ResultConstant.FAILED, "the name already exists");
        }
        return new Result(ResultConstant.SUCCESS, "the name not exists");
    }

    @ApiOperation(value = "根据区Id模糊查找安装点", notes = "根据区Id模糊查找安装点")
    @RequestMapping(value = "/findByRegionIdLike", method = RequestMethod.GET)
    public Result findByRegionIdLike(@RequestParam("id") String id) {
        List<TinstallSite> sites = installSiteService.getSitesByRegionIdLike(id);
        return new Result(ResultConstant.SUCCESS, sites);
    }

    @ApiOperation(value = "根据市Id查找安装点", notes = "根据市Id查找安装点")
    @RequestMapping(value = "/findByCityId", method = RequestMethod.GET)
    public Result findByCityId(@RequestParam("id") String id,
                               @RequestParam(value = "status", defaultValue = "true") Boolean status) {
        List<TinstallSite> sites = installSiteService.getSitesByCityId(Integer.valueOf(id), status);
        return new Result(ResultConstant.SUCCESS, sites);
    }

    @ApiOperation(value = "根据区Id查找安装点", notes = "根据区Id查找安装点")
    @RequestMapping(value = "/findByRegionId", method = RequestMethod.GET)
    public Result findByRegionId(@RequestParam("id") Integer id,
                                 @RequestParam(value = "status", defaultValue = "true") Boolean status) {
        List<TinstallSite> sites = installSiteService.getSitesByRegionId(id, status);
        return new Result(ResultConstant.SUCCESS, sites);
    }

    @ApiOperation(value = "修改指定安装点每天最大预约数", notes = "修改指定安装点每天最大预约数")
    @RequestMapping(value = "/setMaxReservationNumBySiteId", method = RequestMethod.POST)
    public Result setMaxReservationNumBySiteId(@RequestParam("siteId") Integer siteId,
                                               @RequestParam("value") Integer value) {
        installSiteService.setMaxReservationNumBySiteId(siteId, value);
        return new Result(ResultConstant.SUCCESS, "success");
    }

    @ApiOperation(value = "修改指定安装点预约天数", notes = "修改指定安装点预约天数")
    @RequestMapping(value = "/setReservationInDaysNumBySiteId", method = RequestMethod.POST)
    public Result setReservationInDaysNumBySiteId(@RequestParam("siteId") Integer siteId,
                                                  @RequestParam("value") Integer value) {
        installSiteService.setMaxReservationDaysBySiteId(siteId, value);
        return new Result(ResultConstant.SUCCESS, "success");
    }

    @ApiOperation(value = "删除安装点redis缓存", notes = "删除安装点redis缓存")
    @RequestMapping(value = "/deleteRedisData", method = RequestMethod.DELETE)
    public Result deleteRedisData() {
        int count = installSiteService.deleteInstallSiteRedisData();
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "统计安装点数量", notes = "统计安装点数量")
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public Result count(@RequestBody(required = false) InstallSiteQuery query) {
        Long count = installSiteService.countByQuery(query);
        return new Result(ResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "统计安装点每个安装人员安装设备数量", notes = "统计安装点每个安装人员安装设备数量")
    @RequestMapping(value = "/countInstalled", method = RequestMethod.POST)
    public Result countInstall(@RequestBody InstallSiteQuery query) {
        List<InstallSiteAndWorker> result = installSiteService.getSiteInstalledCount(query.getOperatorIdList(), query.getOffset());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "安装点列表导出", notes = "安装点列表导出")
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Result export(@RequestParam("siteId") Integer siteId,HttpServletResponse response) {
        installSiteService.export(siteId, response);
        return new Result(ResultConstant.SUCCESS, "");
    }

}
