package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.constant.enums.exception.RegisterExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DencryptTokenUtil;
import com.szhq.iemp.common.util.MaskUtils;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.Tregistration;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.service.RegistrationService;
import com.szhq.iemp.reservation.api.service.UserService;
import com.szhq.iemp.reservation.api.vo.RegisterVo;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Api(description = "备案模块")
@RestController
@RequestMapping("/register")
public class RegisterationController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterationController.class);

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ElectrmobileService electrmobileService;

    @ApiOperation(value = "备案列表", notes = "根据条件查询备案信息")
    @RequestMapping(value = "/searcher", method = RequestMethod.POST)
    public Result search(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) RegisterQuery query) {
        logger.info("register-query:" + JSONObject.toJSONString(query));
        long start = System.currentTimeMillis();
        MyPage<Tregistration> list = registrationService.findRegistrationCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        if(query.getEncrypt()){
            List<RegisterVo> voList = new ArrayList<>();
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(organMap);
            for (Tregistration organ : list.getContent()) {
                RegisterVo to = modelMapper.map(organ, RegisterVo.class);
                logger.info("vo:{}",to);
                voList.add(to);
            }
            result.put("reservations", voList);
        }else{
            result.put("reservations", list.getContent());
        }
        result.put("total", list.getTotal());
        long end = System.currentTimeMillis();
        long time = end - start;
        logger.info("time:" + time + " ms");
        return new Result(ResultConstant.SUCCESS, result);
    }

    PropertyMap<Tregistration, RegisterVo> organMap = new PropertyMap<Tregistration, RegisterVo>() {
        protected void configure() {
            map().setElectrmobileId(source.getElectrmobileId());
            map().setUserId(source.getUserId());
            map().setIdNumber(source.getIdNumber());
            map().setImei(source.getImei());
            map().setModelNo(source.getModelNo());
            map().setOperatorId(source.getOperatorId());
            map().setCreateTime(source.getCreateTime());
            map().setUsername(source.getUsername());
            map().setRegisterId(source.getRegisterId());
            map().setPhone(source.getPhone());
            if(source.getUser() != null){
                map().setContactPhone(source.getUser().getContactPhone());
                map().setHome(source.getUser().getHome());
            }
        }
    };
    @ApiOperation(value = "备案列表(App)", notes = "根据条件查询备案信息")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result searchApp(@RequestParam(value = "offset") Integer offset,
                         @RequestParam(value = "pagesize") Integer limit,
                         @RequestParam(required = false, value = "sort") String sort,
                         @RequestParam(required = false, value = "order") String order,
                         @RequestBody(required = false) RegisterQuery query) {
        logger.info("app-register-query:" + JSONObject.toJSONString(query));
        MyPage<Tregistration> list = registrationService.findRegistrationCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        List<Tregistration> lists = new ArrayList<>();
        for(Tregistration register : list.getContent()){
            register.setUser(userService.findById(register.getUserId()));
            register.setElectrombile(electrmobileService.findByElecId(register.getElectrmobileId()));
            register.setRegistrationId(register.getRegisterId());
            lists.add(register);
        }
        result.put("reservations", lists);
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "备案注册", notes = "备案注册")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result createRegistration(@RequestBody Tregistration data,
                                     @RequestParam(value = "isCreateUser", defaultValue = "true") Boolean isCreateUser, BindingResult result) {
        return addRegister(data, result, isCreateUser);
    }

    @ApiOperation(value = "备案注册(App)", notes = "备案注册")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Result createRegistrationApp(@Valid @RequestBody Tregistration data, BindingResult result) {
        return addRegister(data, result, true);
    }

    @ApiOperation(value = "备案编辑", notes = "备案编辑")
    @ApiImplicitParam(name = "registration", value = "备案实体", required = true, dataType = "Tregistration")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateRegistration(@RequestBody Tregistration registration) {
        int i = registrationService.updateRegistration(registration);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, ResultConstant.SUCCESS.getMessage());
        }
        return new Result(ResultConstant.FAILED, "修改备案失败");
    }

    @ApiOperation(value = "更换设备", notes = "更换设备")
    @RequestMapping(value = "/changeImei", method = RequestMethod.POST)
    public Result changeImei(@RequestParam(value = "registrationId") Long id,
                             @RequestParam(value = "newImei") String newImei,
                             @RequestBody(required = false) RegisterQuery query) {
        Integer i = registrationService.changeImei(id, newImei, query);
        if (i == 1) {
            return new Result(ResultConstant.SUCCESS, "更换成功");
        }
        return new Result(ResultConstant.FAILED, "更换失败");
    }

    @ApiOperation(value = "根据Id查看备案详情", notes = "根据Id查看备案详情")
    @RequestMapping(value = "/getInfoById", method = RequestMethod.GET)
    public Result getInfoById(@RequestParam(value = "registrationId") Long id,
                              @RequestParam(value = "isEncrypt", defaultValue = "false") Boolean isEncrypt) {
        Tregistration register = registrationService.getInfoById(id);
        if(isEncrypt){
            Tuser user = register.getUser();
            if(user != null){
                String idNumber = MaskUtils.maskIDCardNo(user.getIdNumber());
                String home = MaskUtils.maskHome(user.getHome());
                String phone = MaskUtils.maskPhone(user.getPhone());
                user.setIdNumber(idNumber);
                user.setHome(home);
                user.setPhone(phone);
                register.setUser(user);
                register.setIdNumber(idNumber);
                register.setPhone(phone);
            }
        }
        return new Result(ResultConstant.SUCCESS, register);
    }

    @ApiOperation(value = "根据phone查看备案详情", notes = "根据phone查看备案详情")
    @RequestMapping(value = "/getInfoByPhone", method = RequestMethod.GET)
    public Result getInfoByPhone(@RequestParam(value = "phone") String phone) {
        Tregistration register = registrationService.getInfoByPhone(phone);
        return new Result(ResultConstant.SUCCESS, register);
    }

    @ApiOperation(value = "删除备案(旧)", notes = "删除备案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "备案id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "imei", value = "imei", required = true, dataType = "String")
    })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result deleteRegistration(@RequestParam("id") Long id,
                                     @RequestParam("imei") String imei) {
        registrationService.deleteRegistration(id, imei, true);
        return new Result(ResultConstant.SUCCESS, "删除备案成功");
    }

    @ApiOperation(value = "删除备案", notes = "删除备案")
    @RequestMapping(value = "/deleteById", method = RequestMethod.DELETE)
    public Result deleteById(@RequestParam("id") Long id) {
        Integer i = registrationService.deleteRegister(id);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "导出备案", notes = "根据时间导出备案数据")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void excel(@RequestParam("startTime") Date startTime,
                      @RequestParam("endTime") Date endTime, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RegisterQuery query = new RegisterQuery();
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        List<Integer> operatorIds = DencryptTokenUtil.getOperatorIds(request);
        logger.info("export register operatorIds:" + JSONObject.toJSONString(operatorIds));
        if (operatorIds != null && operatorIds.get(0) != 0) {
            logger.debug("");
            query.setOperatorIdList(operatorIds);
        }
        logger.info("export query:" + JSONObject.toJSONString(query));
        registrationService.exportExcel(response, query);
    }

    @ApiOperation(value = "添加备案(存在用户时添加车辆或不存在用户时车辆用户同时添加(imei必填))", notes = "添加备案(存在用户时添加车辆或不存在用户时同时添加)")
    @RequestMapping(value = "/addRegistration", method = RequestMethod.POST)
    public Result addRegistration(@Valid @RequestBody Tregistration data, BindingResult result) {
        if (result.hasErrors() && result.getFieldError() != null) {
            logger.error("register error." + result.getFieldError().getDefaultMessage());
            return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
        }
        if (data.getElectrombile() == null || StringUtils.isEmpty(data.getElectrombile().getImei())) {
            throw new NbiotException(400, "");
        }
        logger.info("addRegistration-data:" + JSONObject.toJSONString(data));
        Tregistration registration = registrationService.addRegistration(data);
        if (registration == null) {
            return new Result(ResultConstant.FAILED, RegisterExceptionEnum.E_0006.getMessage());
        }
        return new Result(ResultConstant.SUCCESS, registration);
    }

    @ApiOperation(value = "删除备案缓存", notes = "删除备案缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis() {
        registrationService.deleteRegisterRedis();
        return new Result(ResultConstant.SUCCESS, "");
    }

    @ApiOperation(value = "统计备案数量", notes = "统计备案数量")
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public Result count(@RequestBody(required = false) RegisterQuery query) {
        Long count = registrationService.countByQuery(query);
        return new Result(ResultConstant.SUCCESS, count);
    }
    
    private Result addRegister(@RequestBody @Valid Tregistration data, BindingResult result, boolean isCreateUser) {
        if (result.hasErrors() && result.getFieldError() != null) {
            logger.error("register error." + result.getFieldError().getDefaultMessage());
            return new Result(ResultConstant.FAILED, result.getFieldError().getDefaultMessage());
        }
        logger.info("register data:" + JSONObject.toJSONString(data));
        Tregistration registration = registrationService.register(data, isCreateUser);
        if (registration != null) {
            return new Result(ResultConstant.SUCCESS, registration);
        }
        return new Result(ResultConstant.FAILED, "备案失败");
    }

}
