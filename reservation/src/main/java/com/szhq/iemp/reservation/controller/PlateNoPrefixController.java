package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DencryptTokenUtil;
import com.szhq.iemp.common.util.ListUtils;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.api.vo.NbiotRtDataVo;
import com.szhq.iemp.reservation.api.vo.Region;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.service.NbiotDeviceRtDataServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电动车车牌前缀接口
 * @author wanghao
 */
@Api(value = "/platenoPrefix", description = "车牌前缀模块")
@RestController
@Slf4j
@RequestMapping("/platenoPrefix")
public class PlateNoPrefixController {

    @Autowired
    private PlateNoPrefixService plateNoPrefixService;

    @ApiOperation(value = "新增车牌号前缀", notes = "新增车牌号前缀")
    @RequestMapping(value = "/addPlateNoPrefixs", method = RequestMethod.PUT)
    public Result addPlateNoPrefixs(TplatenoPrefix entity) {
        TplatenoPrefix result = plateNoPrefixService.save(entity);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "编辑车牌号前缀", notes = "编辑车牌号前缀")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result update(TplatenoPrefix entity) {
        if(entity == null){
            log.error("parameter can not be null.");
            throw new NbiotException(400, "参数错误");
        }
        if(entity.getId() == null){
            log.error("id can not be null.");
            throw new NbiotException(400, "参数错误");
        }
        TplatenoPrefix prefix = plateNoPrefixService.findById(entity.getId());
        BeanUtils.copyProperties(entity, prefix, PropertyUtil.getNullProperties(entity));
        TplatenoPrefix result = plateNoPrefixService.save(prefix);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "根据省市区Id查找车牌号前缀", notes = "根据省市区Id查找车牌号前缀")
    @RequestMapping(value = "/getPlateNoPrefixByAddressId", method = RequestMethod.POST)
    public Result getPlateNoPrefixByRegionId(@RequestBody Region region) {
        List<TplatenoPrefix> result = plateNoPrefixService.findAllPlateNoPrefixByQuery(region);
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "删除车牌号前缀", notes = "删除车牌号前缀")
    @RequestMapping(value = "/deletePlateNoPrefixs", method = RequestMethod.DELETE)
    public Result deletePlateNoPrefixs(@RequestParam(value = "id") Integer id) {
        Integer count = plateNoPrefixService.deleteById(id);
        return new Result(ResultConstant.SUCCESS, count);
    }

}
