package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.constant.enums.exception.*;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.TcommonConfig;
import com.szhq.iemp.reservation.api.model.TinstallSite;
import com.szhq.iemp.reservation.api.model.Treservation;
import com.szhq.iemp.reservation.api.service.CommonService;
import com.szhq.iemp.reservation.api.service.InstallSiteService;
import com.szhq.iemp.reservation.api.service.ReservationService;
import com.szhq.iemp.reservation.api.vo.query.ReservationQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 预约
 */
@Api(description = "预约模块")
@RestController
@RequestMapping("/reservation")
@Slf4j
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Value("${ismust}")
    private boolean ismust;
    @Value("${nbiot.registration.reservation.day.pm.quota}")
    private String NBIOT_REGISTRATION_RESERVATION_DAY_PM_QUOTA;
    @Value("${nbiot.registration.reservation.day.am.quota}")
    private String NBIOT_REGISTRATION_RESERVATION_DAY_AM_QUOTA;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private InstallSiteService installSiteService;

    @ApiOperation(value = "备案预约列表", notes = "备案预约列表")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Object getReservationSearch(@RequestParam(value = "offset") Integer offset,
                                       @RequestParam(value = "pagesize") Integer limit,
                                       @RequestParam(required = false, value = "sort") String sort,
                                       @RequestParam(required = false, value = "order") String order,
                                       @RequestBody(required = false) ReservationQuery query
    ) {
        log.info("reservation-query：" + JSONObject.toJSONString(query));
        MyPage<Treservation> list = reservationService.findReservationCriteria(offset, limit, sort, order, query);
        Map<String, Object> result = new HashMap<>();
        result.put("reservations", list.getContent());
        result.put("total", list.getTotal());
        return new Result(ResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "备案预约新增", notes = "备案预约新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result save(@RequestBody Treservation entity) {
        logger.info("reservation entity:" + JSONObject.toJSONString(entity));
        Integer installSiteId = entity.getInstallSiteId();
        String reservationNumber = null;
        validMoterNoAndVin(entity);
        if (installSiteId != null) {
            TinstallSite installSite = installSiteService.findById(installSiteId);
            if (installSite == null) {
                throw new NbiotException(SiteExceptionEnum.E_0005.getCode(), SiteExceptionEnum.E_0005.getMessage());
            }
            Long reservationTime = entity.getReservationTime();
            validateReservationTimeAndLimit(installSite, reservationTime);
            reservationNumber = new SimpleDateFormat("yyyyMMdd").format(new Date(reservationTime)) + String.format("%02d", installSiteId) + String.format("%05d", new Random().nextInt(10000) + 1);
            entity.setReservationNumber(reservationNumber);
            entity.setInstallSiteId(installSite.getInstallSiteId());
            entity.setInstallSiteName(installSite.getName());
        } else {
            reservationNumber = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()) + String.format("%02d", new Random().nextInt(10000) + 1) + String.format("%05d", new Random().nextInt(10000) + 1);
            entity.setReservationNumber(reservationNumber);
        }
        Integer count = reservationService.save(entity);
        if (count > 0) {
            JSONObject result = new JSONObject();
            result.put("reservation_number", reservationNumber);
            return new Result(ResultConstant.SUCCESS, result);
        }
        logger.error("save reservation failed", entity);
        return new Result(ResultConstant.FAILED, ReservationExceptionEnum.E_0004.getMessage());
    }


    @ApiOperation(value = "备案预约编辑", notes = "备案预约编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result edit(@RequestBody Treservation entity) {
        Integer i = reservationService.update(entity);
        if (i > 0) {
            return new Result(ResultConstant.SUCCESS, i);
        }
        return new Result(ResultConstant.FAILED, i);
    }

    @ApiOperation(value = "通过预约号获取预约信息", notes = "通过预约号获取预约信息")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public Result getRegistrationReservation(@RequestParam(required = false, value = "number") String number) {
        if (StringUtils.isEmpty(number)) {
            return new Result(ResultConstant.SUCCESS, "");
        }
        Treservation entity = reservationService.getInfo(number);
        if (entity != null) {
            return new Result(ResultConstant.SUCCESS, entity);
        }
        return new Result(ResultConstant.FAILED, "");
    }

    @ApiOperation(value = "通过登录账号获取预约信息", notes = "通过登录账号获取预约信息")
    @RequestMapping(value = "/getByPhone", method = RequestMethod.GET)
    public Result getByPhone(@RequestParam(value = "phone") String phone) {
        Treservation entity = reservationService.findByPhone(phone);
        return new Result(ResultConstant.SUCCESS, entity);
    }

    @ApiOperation(value = "备案预约删除", notes = "备案预约删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result delete(@RequestParam("id") Integer id) {
        reservationService.delete(id);
        return new Result(ResultConstant.SUCCESS, "");
    }

    @ApiOperation(value = "根据预约号删除", notes = "根据预约号删除")
    @RequestMapping(value = "/deleteByReserNo", method = RequestMethod.DELETE)
    public Result deleteByReserNo(@RequestParam("reservationNo") String reservationNo) {
        int i = reservationService.deleteByReserNo(reservationNo);
        return new Result(ResultConstant.SUCCESS, i);
    }

    @ApiOperation(value = "清除预约缓存", notes = "清除预约缓存")
    @RequestMapping(value = "/deleteRedis", method = RequestMethod.DELETE)
    public Result deleteRedis() {
        reservationService.deleteRedisKey();
        return new Result(ResultConstant.SUCCESS, "");
    }


    private boolean validateReservationTimeAndLimit(TinstallSite installSite, Long time) {
        validIsInDays(installSite.getMaxReservationDay(), time);
        long dayStart = TimeStampUtil.getDayStartTs(time);
        long dayEnd = TimeStampUtil.getDayEndTs(time);
        TcommonConfig commonConfig = commonService.findByName(CommonConstant.MAX_RESERVATION_NUM_KEY);
        Integer count = installSite.getMaxReservationCount();
        if (count == null) {
            count = Integer.valueOf(commonConfig.getValue());
        }
        if (reservationService.countByExample(installSite.getInstallSiteId(), dayStart, dayEnd) > count) {
            logger.error("siteId:{},dayStart:{},dayEnd:{},count:{}", installSite.getInstallSiteId(), dayStart, dayEnd, count);
            throw new NbiotException(ReservationExceptionEnum.E_0003.getCode(), ReservationExceptionEnum.E_0003.getMessage());
        }
        return true;
    }

    /**
     * 验证是否在指定时间内
     */
    private void validIsInDays(Integer day, Long time) {
        TcommonConfig commonConfig = commonService.findByName(CommonConstant.RESERVATION_IN_DAYS_KEY);
        if (day == null) {
            day = Integer.valueOf(commonConfig.getValue());
        }
        logger.info("reservation-in-days:" + day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, day);
        Date afterDays = calendar.getTime();
        if (afterDays.getTime() < time) {
            throw new NbiotException(500001, "预约时间只能在" + day + "日内", day);
        } else {
            logger.error("afterTime:{},time:{}", new Date(afterDays.getTime()), new Date(time));
        }
    }

    /**
     * 验证电机号及车架号
     */
    private void validMoterNoAndVin(Treservation entity) {
        if (StringUtils.isNotEmpty(entity.getMotorNumber())) {
            List<Treservation> reservation = reservationService.findByMotorNumber(entity.getMotorNumber());
            if (reservation != null && reservation.size() > 0) {
                //RegisterExceptionEnum.E_00014
                throw new NbiotException(10000008, "");
            }
        }
        if (StringUtils.isNotEmpty(entity.getVin())) {
            Treservation tnbiotRegistrationReservation = reservationService.findByVin(entity.getVin());
            if (tnbiotRegistrationReservation != null) {
                //ElectrombileExceptionEnum.E_00012
                throw new NbiotException(3000009, "");
            }
        }
    }


    private boolean validateRegistrationTime(Integer registrationSiteId, Long time) {
        int apm = TimeStampUtil.getApm(time);
        long dayStart = TimeStampUtil.getDayStartTs(time);
        long dayEnd = TimeStampUtil.getDayEndTs(time);
        long dayAmEnd = TimeStampUtil.getDayAmEndTs(time);
        long dayPmStart = TimeStampUtil.getDayPmStartTs(time);
        long start;
        long end;
        int quota;
        if (1 == apm) {
            start = dayPmStart;
            end = dayEnd;
            quota = Integer.valueOf(NBIOT_REGISTRATION_RESERVATION_DAY_PM_QUOTA);
        } else {
            start = dayStart;
            end = dayAmEnd;
            quota = Integer.valueOf(NBIOT_REGISTRATION_RESERVATION_DAY_AM_QUOTA);
        }
        if (reservationService.countByExample(registrationSiteId, start, end) >= quota) {
            return false;
        }
        return true;
    }
}
