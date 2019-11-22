package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ListUtils;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.ElecmobileUserService;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.NbiotDeviceRtDataService;
import com.szhq.iemp.reservation.api.service.UserService;
import com.szhq.iemp.reservation.api.vo.NbiotRtDataVo;
import com.szhq.iemp.reservation.api.vo.TelectrmobileVo;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import com.szhq.iemp.reservation.service.NbiotDeviceRtDataServiceImpl;
import com.szhq.iemp.reservation.util.DecyptTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 电动车相关接口
 * @author wanghao
 */
@Api(value = "/electrombile", description = "电动车模块")
@RestController
@RequestMapping("/electrombile")
public class ElectrombileController {
    private static final Logger logger = LoggerFactory.getLogger(ElectrombileController.class);

    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private ElecmobileUserService elecmobileUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private NbiotDeviceRtDataService nbiotDeviceRtDataService;

    private NbiotDeviceRtDataServiceImpl rt = new NbiotDeviceRtDataServiceImpl();

    @ApiOperation(value = "电动车列表查询", notes = "电动车列表查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) ElecmobileQuery elecQuery) {
        logger.info("elec query:" + JSONObject.toJSONString(elecQuery));
        MyPage<Telectrmobile> list = electrombileService.findElecByCriteria(offset, limit, sort, order, elecQuery);
        JSONArray resultArray = new JSONArray();
        if (list != null && list.getTotal() > 0) {
            for (Telectrmobile elec : list.getContent()) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(elec));
                if (object.getString("imei") == null) continue;
                String imei = object.getString("imei");
                if (StringUtils.isEmpty(imei)) continue;
                NbiotDeviceRtData data = nbiotDeviceRtDataService.getLocation(imei);
                if (data != null) {
                    object.put("data", JSONObject.parseObject(JSON.toJSONString(rt.dataTransfer(data))));
                }
                resultArray.add(object);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("electrombiles", resultArray);
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "所有电动车列表查询(包括未绑定设备车辆)", notes = "电动车列表查询(包括未绑定设备车辆)")
    @RequestMapping(value = "/searchAll", method = RequestMethod.POST)
    public Result searchAll(@RequestParam(value = "offset") Integer offset,
                            @RequestParam(value = "pagesize") Integer limit,
                            @RequestParam(required = false, value = "sort") String sort,
                            @RequestParam(required = false, value = "order") String order,
                            @RequestBody(required = false) RegisterQuery elecQuery) {
        logger.info("all-elec-query:" + JSONObject.toJSONString(elecQuery));
        MyPage<TelectrmobileVo> list = electrombileService.findAllElecByCriteria(offset, limit, sort, order, elecQuery);
        Map<String, Object> result = new HashMap<>();
        result.put("electrombiles", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }


    @ApiOperation(value = "根据userId获取用户所有已绑设备电动车信息", notes = "根据userId获取用户所有已绑设备电动车信息")
    @RequestMapping(value = "/getAllElecmobileByUserId", method = RequestMethod.GET)
    public Result getAllElecmobileByUserId(@RequestParam(value = "userId") String userId,
                                           @RequestParam(value = "type", required = false) String type) {
        List<Telectrmobile> elecmobiles = new ArrayList<>();
        if (StringUtils.isEmpty(type)) {
            elecmobiles = electrombileService.getAllElecmobileByUserId(userId);
        } else {
            elecmobiles = electrombileService.getAllElecmobileByUserIdAndType(userId, type);
        }
        String[] sortNameArr = {"createTime"};
        boolean[] isAscArr = {false};
        ListUtils.sort(elecmobiles, sortNameArr, isAscArr);
        return new Result(ResultConstant.SUCCESS, elecmobiles);
    }

    @ApiOperation(value = "根据userId获取用户所有电动车实时轨迹", notes = "根据userId获取用户所有电动车实时轨迹")
    @RequestMapping(value = "/getAllElecmobileLocationByUserId", method = RequestMethod.GET)
    public Result getAllLocationByUserId(@RequestParam(value = "userId") String userId,
                                         @RequestParam(value = "app", required = false) boolean isApp,
                                         @RequestParam(value = "type", required = false, defaultValue = "W302") String type) {
        List<Telectrmobile> elecmobiles = electrombileService.getAllElecmobileByUserIdAndType(userId, type);
        if (elecmobiles != null && !elecmobiles.isEmpty()) {
            List<String> imeis = elecmobiles.stream().map(Telectrmobile::getImei).collect(Collectors.toList());
            logger.info("elec-imeis:" + JSONObject.toJSONString(imeis));
            List<NbiotDeviceRtData> list = new ArrayList<>();
            List<NbiotRtDataVo> voList = new ArrayList<>();
            if (isApp == true) {
                voList = nbiotDeviceRtDataService.findRtDataByElecs(elecmobiles);
                String[] sortNameArr = {"createTime"};
                boolean[] isAscArr = {false};
                ListUtils.sort(voList, sortNameArr, isAscArr);
//				voList = voList.stream().sorted(Comparator.comparing(NbiotRtDataVo::getTime, Comparator.nullsLast((o1,o2)->o2.compareTo(o1)))).collect(Collectors.toList());
                return new Result(ResultConstant.SUCCESS, voList);
            } else {
                list = nbiotDeviceRtDataService.findByImeiIn(elecmobiles);
                String[] sortNameArr = {"createTime"};
                boolean[] isAscArr = {false};
                ListUtils.sort(list, sortNameArr, isAscArr);
                return new Result(ResultConstant.SUCCESS, list);
            }
        }
        return new Result(ResultConstant.SUCCESS, elecmobiles);
    }

    @ApiOperation(value = "根据imei获取用户电动车实时轨迹", notes = "根据imei获取用户电动车实时轨迹")
    @RequestMapping(value = "/getLocationByImei", method = RequestMethod.GET)
    public Result getElecmobileLocationByImei(@RequestParam(value = "imei") String imei,
                                              @RequestParam(value = "app", required = false) boolean isApp) {
        Telectrmobile elecmobile = electrombileService.findByImei(imei);
        if (elecmobile != null) {
            List<Telectrmobile> elecmobiles = new ArrayList<>();
            elecmobiles.add(elecmobile);
            if (isApp == true) {
                List<NbiotRtDataVo> voList = nbiotDeviceRtDataService.findRtDataByElecs(elecmobiles);
                String[] sortNameArr = {"createTime"};
                boolean[] isAscArr = {false};
                ListUtils.sort(voList, sortNameArr, isAscArr);
                return new Result(ResultConstant.SUCCESS, voList);
            } else {
                List<NbiotDeviceRtData> list = nbiotDeviceRtDataService.findByImeiIn(elecmobiles);
                String[] sortNameArr = {"createTime"};
                boolean[] isAscArr = {false};
                ListUtils.sort(elecmobiles, sortNameArr, isAscArr);
                return new Result(ResultConstant.SUCCESS, list);
            }
        }
        return new Result(ResultConstant.SUCCESS, elecmobile);
    }

    @ApiOperation(value = "根据imei获取电动车及车主信息", notes = "根据imei获取电动车及车主信息")
    @RequestMapping(value = "/getElecAndUserInfoByImei", method = RequestMethod.GET)
    public Result getElecAndUserInfoByImei(@RequestParam(value = "imei") String imei) {
        List<Tuser> users = new ArrayList<Tuser>();
        Telectrmobile electrombile = electrombileService.findByImei(imei);
        if (electrombile != null) {
            List<TelectrombileUser> electrombileUsers = elecmobileUserService.findByElecId(electrombile.getElectrmobileId());
            if (electrombileUsers != null && electrombileUsers.size() > 0) {
                for (TelectrombileUser electrombileUser : electrombileUsers) {
                    Tuser user = userService.findById(electrombileUser.getUserId());
                    users.add(user);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("electrombile", electrombile);
        result.put("user", users);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据imei获取电动车及位置信息", notes = "根据imei获取电动车及位置信息")
    @RequestMapping(value = "/getElecAndLocationInfoByImei", method = RequestMethod.GET)
    public Result getElecAndLocationInfo(@RequestParam(value = "imei") String imei, HttpServletRequest request) {
        List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
        Telectrmobile elecmobile = electrombileService.findByImei(imei);
        NbiotDeviceRtData deviceRtData = nbiotDeviceRtDataService.findByImei(imei, operatorIds);
        Map<String, Object> result = new HashMap<>();
        if (elecmobile != null && elecmobile.getUser() != null) {
            Tuser user = userService.findById(elecmobile.getUser().getId());
            result.put("owner", JSONObject.toJSONString(user));
        }
        result.put("elecmobile", elecmobile);
        result.put("location", deviceRtData);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据imeis获取车牌号", notes = "根据imeis获取车牌号")
    @RequestMapping(value = "/getPlateNoByImeis", method = RequestMethod.POST)
    public Result getPlateNoByImeis(@RequestBody List<String> imeis) {
        Map<String, String> map = electrombileService.getPlateNoByImeis(imeis);
        return new Result(ResultConstant.SUCCESS, map);
    }

    @ApiOperation(value = "根据车牌号获取车及用户信息", notes = "根据车牌号获取车及用户信息")
    @RequestMapping(value = "/getElecAndUserInfoByPlateNo", method = RequestMethod.POST)
    public Result getElecAndUserInfoByPlateNo(@RequestBody ElecmobileQuery elecQuery) {
        Telectrmobile electrombile = electrombileService.getElecAndUserInfoByPlateNo(elecQuery);
        return new Result(ResultConstant.SUCCESS, electrombile);
    }

    @ApiOperation(value = "获取布防状态信息", notes = "获取布防状态信息")
    @RequestMapping(value = "/getBfStatus", method = RequestMethod.GET)
    public Result getBfStatus(@RequestParam("imei") String imei) {
        String status = electrombileService.getBfStatus(imei);
        return new Result(ResultConstant.SUCCESS, status);
    }

    @ApiOperation(value = "修改布防状态", notes = "修改布防状态")
    @RequestMapping(value = "/updateBfStatus", method = RequestMethod.POST)
    public Result updateBfStatus(@RequestParam(value = "imei") String imei, @RequestParam(value = "mode") boolean mode) {
        logger.info("imei:" + imei + ",mode:" + mode);
        Integer i = electrombileService.updateBfStatus(imei, mode);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "修改布控状态", notes = "修改布控状态")
    @RequestMapping(value = "/updateBkStatus", method = RequestMethod.POST)
    public Result updateBkStatus(@RequestParam(value = "imei") String imei, @RequestParam(value = "mode") boolean mode) {
        logger.info("imei:" + imei + ",mode:" + mode);
        Integer i = electrombileService.updateBkStatus(imei, mode);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, null);
    }

    @ApiOperation(value = "修改设备名称", notes = "修改设备名称")
    @RequestMapping(value = "/setName", method = RequestMethod.POST)
    public Result setName(@RequestParam(value = "imei") String imei, @RequestParam(value = "name") String name) {
        electrombileService.setNameByImei(imei, name);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "修改设备频率", notes = "修改设备频率")
    @RequestMapping(value = "/setFrequency", method = RequestMethod.POST)
    public Result setFrequency(@RequestParam(value = "imei") String imei, @RequestParam(value = "frequency") Integer frequency) {
        electrombileService.setFrequencyByImei(imei, frequency);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "导入excel批量修改保单号", notes = "导入excel批量修改保单号")
    @RequestMapping(value = "/batchImportExcelUpdatePolicyNo", method = RequestMethod.POST)
    public Result batchImportExcelUpdatePolicyNo(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long size = file.getSize();
        if (StringUtils.isEmpty(fileName) || size == 0) {
            logger.error("file is null");
            throw new NbiotException(500, "Excel文件不能为空");
        }
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            logger.error("Excel文件格式不正确");
            throw new NbiotException(500, "Excel文件格式不正确");
        }
        electrombileService.batchImportExcelUpdatePolicyNo(file);
        return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
    }

    @ApiOperation(value = "根据索引查找厂商", notes = "根据索引查找vendors")
    @RequestMapping(value = "/getTypeByIndex", method = RequestMethod.GET)
    public Result getTypeByIndex(@RequestParam(value = "index") String index) {
        List<TelectrombileVendor> elecmobileVendors = electrombileService.getTypeByIndex(index);
        return new Result(ResultConstant.SUCCESS, elecmobileVendors);
    }

    @ApiOperation(value = "获取所有电动车厂商", notes = "获取所有电动车厂商")
    @RequestMapping(value = "/getAllElecmobileVendor", method = RequestMethod.GET)
    public Result getAllElecmobileVendor() {
        List<TelectrombileVendor> vendors = electrombileService.getElecmobileVendors();
        return new Result(ResultConstant.SUCCESS, vendors);
    }

    @ApiOperation(value = "获取所有电动车颜色", notes = "获取所有电动车颜色")
    @RequestMapping(value = "/getAllElecmobileColors", method = RequestMethod.GET)
    public Result getAllElecmobileColors() {
        List<TelectrombileColor> colors = electrombileService.getElecmobileColors();
        return new Result(ResultConstant.SUCCESS, colors);
    }

    @ApiOperation(value = "获取所有电动车类型", notes = "获取所有电动车类型")
    @RequestMapping(value = "/getAllElecmobileTypes", method = RequestMethod.GET)
    public Result getAllElecmobileTypes() {
        List<TelectrombileType> types = electrombileService.getElecmobileTypes();
        return new Result(ResultConstant.SUCCESS, types);
    }

    @ApiOperation(value = "添加电动车颜色", notes = "添加电动车颜色")
    @RequestMapping(value = "/addColor", method = RequestMethod.PUT)
    public Result addColor(TelectrombileColor entity) {
        TelectrombileColor color = electrombileService.addColors(entity);
        return new Result(ResultConstant.SUCCESS, color);
    }

    @ApiOperation(value = "添加电动车品牌", notes = "添加电动车品牌")
    @RequestMapping(value = "/addVendor", method = RequestMethod.PUT)
    public Result addVendor(TelectrombileVendor entity) {
        TelectrombileVendor vendor = electrombileService.addVendors(entity);
        return new Result(ResultConstant.SUCCESS, vendor);
    }

    @ApiOperation(value = "添加电动车类型", notes = "添加电动车类型")
    @RequestMapping(value = "/addType", method = RequestMethod.PUT)
    public Result addType(TelectrombileType entity) {
        TelectrombileType type = electrombileService.addTypes(entity);
        return new Result(ResultConstant.SUCCESS, type);
    }

    @ApiOperation(value = "删除电动车颜色", notes = "删除电动车颜色")
    @RequestMapping(value = "/deleteColor", method = RequestMethod.DELETE)
    public Result deleteColor(@RequestParam(value = "id") Integer id) {
        Integer i = electrombileService.deleteElecColorById(id);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "删除电动车品牌", notes = "删除电动车品牌")
    @RequestMapping(value = "/deleteVendor", method = RequestMethod.DELETE)
    public Result deleteVendor(@RequestParam(value = "id") Integer id) {
        Integer i = electrombileService.deleteElecVendorById(id);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "删除电动车类型", notes = "删除电动车类型")
    @RequestMapping(value = "/deleteType", method = RequestMethod.DELETE)
    public Result deleteType(@RequestParam(value = "id") Integer id) {
        Integer i = electrombileService.deleteElecTypeById(id);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "删除电动车redis缓存", notes = "删除电动车redis缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis() {
        Integer i = electrombileService.deleteElecRedisData();
        Integer j = electrombileService.deleteRedisColorTypeVendor();
        return new Result(ResultConstant.SUCCESS, i);
    }

}
