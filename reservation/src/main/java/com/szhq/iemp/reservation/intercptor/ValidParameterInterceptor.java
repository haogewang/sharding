package com.szhq.iemp.reservation.intercptor;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.RegisterExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DecyptTokenUtil;
import com.szhq.iemp.common.util.ListUtils;
import com.szhq.iemp.reservation.api.model.TdeviceInventory;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tregistration;
import com.szhq.iemp.reservation.api.service.DeviceInventoryService;
import com.szhq.iemp.reservation.api.service.ElectrmobileService;
import com.szhq.iemp.reservation.api.vo.NotrackerRegister;
import com.szhq.iemp.reservation.vo.BodyReaderRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 验证权限拦截器
 * @author wanghao
 */
public class ValidParameterInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ValidParameterInterceptor.class);

    private ElectrmobileService electrombileService;
    private DeviceInventoryService deviceInventoryService;

    /**
     * 进入Controller之前开始拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("valid parameter Interceptor preHandle...");
        if (handler instanceof HandlerMethod) {
            String method = ((HandlerMethod) handler).getMethod().getName();
            electrombileService = getDAO(ElectrmobileService.class, request);
            deviceInventoryService = getDAO(DeviceInventoryService.class, request);
            validURL(request, method);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

    /**
     * 验证URL及赋值
     */
    private void validURL(HttpServletRequest request, String method) {
        //请求参数（不包括body体）
        String paramData = JSONUtils.toJSONString(ListUtils.getQueryParams(request.getParameterMap()));
        String body = new BodyReaderRequestWrapper(request).getBodyString();
        Map<String, Object> map = DecyptTokenUtil.decyptToken(request);
        if (map != null && map.size() > 0) {
            if (map.get("operatorIdList") != null) {
                JSONObject parameterJson = JSONObject.parseObject(paramData);
                List<Integer> operatorIdList = JSONObject.parseArray(map.get("operatorIdList").toString(), Integer.class);
//                logger.info("requestMethod:" + request.getMethod() + ", URL:" + request.getRequestURI() + ",method:" + method + ",parameterJson:" + parameterJson + ",operatorIdList:" + JSONObject.toJSONString(operatorIdList));
                logger.info("URL:{},method:{},parameterJson:{},body:{},operatorIdList:{}",request.getRequestURI(), method, parameterJson, body, JSONObject.toJSONString(operatorIdList));
                if (map.get("role") != null && "2".equals((String) map.get("role"))) {
                    logger.info("role is 2.");
                    return;
                }
                if (operatorIdList == null || operatorIdList.isEmpty()) {
                    logger.error("not belong any operator.no right.");
                    throw new NbiotException(600014, OperatorExceptionEnum.E_00015.getMessage());
                }
                if(operatorIdList.get(0) == 0) {
                    logger.info("admin user, no need authentication");
                    return;
                }
                //添加电动车或备案
                if (request.getRequestURI() != null && (request.getRequestURI().endsWith(CommonConstant.NOTRACKER_ADD_REGISTER))) {
                    NotrackerRegister notrackerRegister = JSONObject.parseObject(body, NotrackerRegister.class);
                    if (notrackerRegister.getElec() != null && StringUtils.isNotEmpty(notrackerRegister.getElec().getImei())) {
                        TdeviceInventory deviceInventory = deviceInventoryService.findByImei(notrackerRegister.getElec().getImei());
                        validDevice(operatorIdList, notrackerRegister.getElec().getImei(), deviceInventory);
                    }
                }
                //绑定设备
                if (request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.NOTRACKER_BOUND)) {
                    String type = (String) parameterJson.get("type");
                    if (StringUtils.isEmpty(type)) {
                        logger.info("type is:{}", type);
                        return;
                    }
                    String imei = (String) parameterJson.get("imei");
                    TdeviceInventory deviceInventory = deviceInventoryService.findByImei(imei);
                    validDevice(operatorIdList, imei, deviceInventory);
                    return;
                }
                //解绑设备
                if (request.getRequestURI() != null &&
                        //310解绑设备
                        (request.getRequestURI().endsWith(CommonConstant.NOTRACKER_UNBOUND_DEVICE) ||
                                //删除设备（不删除电动车）
                                request.getRequestURI().endsWith(CommonConstant.NOTRACKER_UNBOUND_NO_DELELEC)
                                )) {
                    String imei = (String) parameterJson.get("imei");
                    Telectrmobile electrombile = electrombileService.findByImei(imei);
                    validElec(operatorIdList, imei, electrombile);
                }
                //备案
                if (request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.REGISTER_URL)) {
                    Tregistration registration = JSONObject.parseObject(body, Tregistration.class);
                    if (registration != null && registration.getElectrmobile() != null
                            && StringUtils.isNotEmpty(registration.getElectrmobile().getImei())) {
                        TdeviceInventory deviceInventory = deviceInventoryService.findByImei(registration.getElectrmobile().getImei());
                        validDevice(operatorIdList, deviceInventory.getImei(), deviceInventory);
                    } else {
                        logger.error("wrong parameter.");
                        throw new NbiotException(400, "");
                    }
                }
                //更换设备
                if (request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.REGISTER_CHANGEIMEI_URL)) {
                    String imei = (String) parameterJson.get("newImei");
                    TdeviceInventory deviceInventory = deviceInventoryService.findByImei(imei);
                    validDevice(operatorIdList, imei, deviceInventory);
                }
                //删除备案
                if (request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.REGISTER_DELETE_URL)) {
                    String imei = (String) parameterJson.get("imei");
                    TdeviceInventory deviceInventory = deviceInventoryService.findByImei(imei);
                    valid(imei, operatorIdList);
                }
            }
        } else {
            logger.error("token decypt is null.");
        }
    }

    /**
     * 验证设备运营公司
     */
    private void validDevice(List<Integer> operatorIdList, String imei, TdeviceInventory deviceInventory) {
        if (deviceInventory == null) {
            logger.error("device not found.imei:{}", imei);
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        if (Objects.equals(1, deviceInventory.getDevstate()) || electrombileService.findByImei(imei) != null) {
            logger.error("device has installed.imei:{}, device status:{}", imei, deviceInventory.getDevstate());
            if (operatorIdList != null && !operatorIdList.contains(deviceInventory.getOperatorId())) {
                logger.error("device is not belong the operator.imei:{}, worker operatorIds:{},operatorId:{}", imei, JSONObject.toJSONString(operatorIdList), deviceInventory.getOperatorId());
                throw new NbiotException(10000010, "该账号所属运营公司与设备运营公司不符");
            }
            throw new NbiotException(10000011, "");
        }
        if(!deviceInventory.getIsActive()){
            logger.error("device is not active.imei:" + deviceInventory.getImei() + "," + deviceInventory.getIsActive());
            throw new NbiotException(DeviceExceptionEnum.E_00033.getCode(), DeviceExceptionEnum.E_00033.getMessage());
        }
        if (operatorIdList.contains(deviceInventory.getOperatorId())) {
            logger.info("valid pass! imei:{}", imei);
        } else {
            logger.error("valid error! imei:{}", imei);
            //RegisterExceptionEnum.E_00016.getMessage()
            throw new NbiotException(10000010, "");
        }

    }

    /**
     * 权限校验
     */
    private void valid(String imei, List<Integer> operatorIds) {
        logger.info("operatorIds:" + JSONObject.toJSONString(operatorIds));
        //admin 不做限制
//		if(operatorIds != null && operatorIds.get(0) == 0) {
//			return;
//		}
        if (operatorIds == null) {
            logger.error("no token");
            return;
        }
        TdeviceInventory deviceInventory = deviceInventoryService.findByImei(imei);
        if (deviceInventory == null) {
            logger.error("device is not exist.imei:" + imei);
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        if (!operatorIds.contains(deviceInventory.getOperatorId())) {
            logger.error("valid error! imei:{}", imei);
            //RegisterExceptionEnum.E_00016.getMessage()
            throw new NbiotException(10000010, "");
        } else {
            logger.info("valid pass! imei:{}", imei);
        }
    }

    /**
     * 验证电动车运营公司
     */
    private void validElec(List<Integer> operatorIdList, String imei, Telectrmobile electrombile) {
        if (electrombile == null) {
            logger.error("the device is not exist!imei:" + imei);
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        if (operatorIdList != null && operatorIdList.contains(electrombile.getOperatorId())) {
            logger.info("valid pass!");
        } else {
            logger.error("valid error! imei:{}, operatorIdList:{}, elecOperId:{}", imei, JSONObject.toJSONString(operatorIdList), electrombile.getOperatorId());
            //RegisterExceptionEnum.E_00016.getMessage()
            throw new NbiotException(10000010, "");
        }
    }

    /**
     * 根据传入的类型获取spring管理的对应dao
     */
    private <T> T getDAO(Class<T> clazz, HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(clazz);
    }

    /**
     * map转化
     */
//    private Map<String, Object> getQueryParams(Map<String, String[]> map) {
//        Map<String, Object> params = new HashMap<String, Object>(map.size());
//        int len;
//        for (Map.Entry<String, String[]> entry : map.entrySet()) {
//            len = entry.getValue().length;
//            if (len == 1) {
//                params.put(entry.getKey(), entry.getValue()[0]);
//            } else if (len > 1) {
//                params.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return params;
//    }

}
