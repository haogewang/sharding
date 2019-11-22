package com.szhq.iemp.device.intercptor;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.GroupExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DecyptTokenUtil;
import com.szhq.iemp.common.util.ListUtils;
import com.szhq.iemp.device.api.model.*;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.vo.BodyReaderRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 参数校验拦截器
 * @author wanghao
 */
public class ParameterValidInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ParameterValidInterceptor.class);

	private DeviceInventoryService deviceInventoryService;
	private InstallSiteService installSiteService;
	private OperatorService operatorService;
	private GroupService groupService;
	private UserService userService;
	/**
	 * 进入Controller之前开始拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.debug("Interceptor preHandle...");
		try {
			if(handler instanceof HandlerMethod){
				String method = ((HandlerMethod) handler).getMethod().getName();
				deviceInventoryService = getDAO(DeviceInventoryService.class, request);
				installSiteService = getDAO(InstallSiteService.class, request);
				operatorService = getDAO(OperatorService.class, request);
				groupService = getDAO(GroupService.class, request);
				userService = getDAO(UserService.class, request);
				validURL(request, method);
			}
		} catch (Exception e) {
			logger.error("e",e);
			throw new NbiotException(500, e.getMessage());
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
		List<Integer> operatorIds = DecyptTokenUtil.getOperatorIds(request);
		JSONObject parameterJson = JSONObject.parseObject(paramData);
		if (operatorIds == null || operatorIds.isEmpty()) {
			logger.error("not belong any operator.no right." + parameterJson);
			throw new NbiotException(401, OperatorExceptionEnum.E_00015.getMessage());
		}
		if( operatorIds.get(0) == 0) {
			logger.info("admin user. no need authentication");
			return;
		}
		logger.info("URL:{},method:{},parameterJson:{},body:{},user's operatorIds:{}",request.getRequestURI(), method, parameterJson, body, JSONObject.toJSONString(operatorIds));
		//验证入库箱号
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.VALID_PUTSTORAGE_BY_BOXNUMBERS_URL)) {
			String boxNumber = parameterJson.getString("boxNumber");
			List<String> list = new ArrayList<>();
			list.add(boxNumber);
			List<Integer> operatorIdList = deviceInventoryService.getOperatorIdsByBoxNumbers(list);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, operatorIdList);
			// && operatorIds.get(0) != 0
			if(operatorIds.get(0) != 0) {
				if(operatorIds.containsAll(operatorIdList)) {
					logger.info("valid pass.boxNumber:" + boxNumber);
					return;
				}else {
					logger.error("valid failed.boxNumber:{},operatorIds:{},targetOperatorIds:{}" ,boxNumber, operatorIds, operatorIdList);
					throw new NbiotException(OperatorExceptionEnum.E_00013.getCode(), OperatorExceptionEnum.E_00013.getMessage());
				}
			}
		}
		//入库验证
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.PUT_INTO_STORAGE_URL)) {
			List<String> boxNumberList = JSONObject.parseArray(body, String.class);
			List<Integer> operatorIdList = deviceInventoryService.getOperatorIdsByBoxNumbers(boxNumberList);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, operatorIdList);
			// && operatorIds.get(0) != 0
			if(operatorIdList.size() > 0 && operatorIds.get(0) != 0) {
				if(operatorIds.containsAll(operatorIdList)) {
					Integer operatorId = DecyptTokenUtil.getOperatorId(request);
					Toperator toperator = operatorService.findById(operatorId);
					if(toperator != null){
						Integer deviceStoreHouseId = parameterJson.getInteger("deviceStorehouseId");
						if(Objects.equals(deviceStoreHouseId, toperator.getStorehouseId())){
							logger.info("valid pass.");
						}else{
							logger.error("valid failed.storeHouse is not right!deviceStoreHouseId：{}，operator storeId:{}", deviceStoreHouseId, toperator.getStorehouseId());
							throw new NbiotException(DeviceExceptionEnum.E_00030.getCode(), DeviceExceptionEnum.E_00030.getMessage());
						}
					}
					return;
				}else {
					logger.error("valid failed.boxNumberList:{},operatorIds:{},targetOperatorIds:{}" ,JSONObject.toJSONString(boxNumberList), operatorIds, operatorIdList);
					throw new NbiotException(OperatorExceptionEnum.E_00013.getCode(), OperatorExceptionEnum.E_00013.getMessage());
				}
			}
		}
		//验证出库设备
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.VALID_IMEI_INFO_URL)) {
			String imei = parameterJson.getString("imei");
			List<String> list = new ArrayList<>();
			list.add(imei);
			List<Integer> targetOperatorIds = deviceInventoryService.getOperatorIdsByImeis(list);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, targetOperatorIds);
			if(targetOperatorIds.size() > 0) {
				if(operatorIds.containsAll(targetOperatorIds)) {
					logger.info("valid pass.imei:" + imei);
					return;
				}else {
					logger.error("valid failed.imei:{},operatorIds:{},targetOperatorIds:{}",imei, operatorIds, targetOperatorIds);
					throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
				}
			}
		}
		//根据imeis分配设备
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.DISPACHE_BY_IMEIS_URL)) {
			List<String> imeis = JSONObject.parseArray(body, String.class);
			Integer installSiteId = parameterJson.getInteger("installSiteId");
			List<Integer> targetOperatorIds = deviceInventoryService.getOperatorIdsByImeis(imeis);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, targetOperatorIds);
			//&& operatorIds.get(0) != 0
			if(targetOperatorIds.size() > 0) {
				if(operatorIds.containsAll(targetOperatorIds)) {
					if (validInstallSiteAndDeviceOperatorId(operatorIds, installSiteId)) return;
				}else {
					logger.error("valid failed.imeis:{},operatorIds:{},targetOperatorIds:{}",JSONObject.toJSONString(imeis),operatorIds, targetOperatorIds);
					throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
				}
			}
		}
		//根据箱号分配设备
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.DISPACHE_BY_BOXNUMBER_URL)){
			List<String> boxNumbers = JSONObject.parseArray(body, String.class);
			Integer installSiteId = parameterJson.getInteger("installSiteId");
			List<Integer> targetOperatorIds = deviceInventoryService.getOperatorIdsByBoxNumbers(boxNumbers);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, targetOperatorIds);
			// && operatorIds.get(0) != 0
			if(targetOperatorIds.size() > 0) {
				if(operatorIds.containsAll(targetOperatorIds)) {
					if (validInstallSiteAndDeviceOperatorId(operatorIds, installSiteId)) return;
				}else {
					logger.error("valid failed.boxNumbers:{},operatorIds:{},targetOperatorIds:{}",JSONObject.toJSONString(boxNumbers), operatorIds, targetOperatorIds);
					throw new NbiotException(OperatorExceptionEnum.E_00013.getCode(), OperatorExceptionEnum.E_00013.getMessage());
				}
			}
		}
		//设备分组
		if(request.getRequestURI() != null && (request.getRequestURI().endsWith(CommonConstant.DISPATCH_TO_DEVICE_GROUP_URL)
												|| request.getRequestURI().endsWith(CommonConstant.DISPATCH_TO_ELEC_GROUP_URL))){
			List<String> imeis = JSONObject.parseArray(body, String.class);
			Integer groupId = parameterJson.getInteger("groupId");
			List<Integer> targetOperatorIds = deviceInventoryService.getOperatorIdsByImeis(imeis);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, targetOperatorIds);
			//&& operatorIds.get(0) != 0
			if(targetOperatorIds.size() > 0) {
				Tgroup group = groupService.findById(groupId);
				if(group == null){
					logger.error("group is not found.id:" + groupId);
					throw new NbiotException(500002, GroupExceptionEnum.E_0004.getMessage());
				}
				if(operatorIds.containsAll(targetOperatorIds)){
					if(operatorIds.contains(group.getOperatorId())){
						logger.info("valid pass.");
						return;
					}else {
						logger.error("group operator is not equals operator");
						throw new NbiotException(500005, GroupExceptionEnum.E_0007.getMessage());
					}
				}else{
					logger.error("valid failed.imeis:" + JSONObject.toJSONString(imeis));
					throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
				}
			}else {
				logger.error("valid failed.imeis:" + JSONObject.toJSONString(imeis));
				throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
			}

		}
		//移除分组
		if(request.getRequestURI() != null && request.getRequestURI().endsWith(CommonConstant.REMOVE_GROUP)){
			List<String> imeis = JSONObject.parseArray(body, String.class);
			List<Integer> targetOperatorIds = deviceInventoryService.getOperatorIdsByImeis(imeis);
			logger.info("operatorIds:{},targetOperatorIds:{}", operatorIds, targetOperatorIds);
			if(targetOperatorIds.size() > 0) {
				if(operatorIds.containsAll(targetOperatorIds)){
					logger.info("valid pass.");
					return;
				}else{
					logger.error("valid failed.imeis:" + JSONObject.toJSONString(imeis));
					throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
				}
			}else {
				logger.error("valid failed.imeis:" + JSONObject.toJSONString(imeis));
				throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
			}
		}
		//激活设备//退货
		if (request.getRequestURI() != null && (request.getRequestURI().endsWith(CommonConstant.DEVICE_ACTIVE_URL) || request.getRequestURI().endsWith(CommonConstant.DEVICE_UN_ACTIVE_URL))) {
			String imei = parameterJson.getString("imei");
			String userId = parameterJson.getString("userId");
			TdeviceInventory device = deviceInventoryService.findByImei(imei);
			Tuser user = userService.findById(userId);
			if(device != null && device.getStorehouseId() != null){
				if(Objects.equals(1, device.getStorehouseId())){
					logger.error("device is not putstoreage.imei:" + device.getImei());
					throw new NbiotException(500, DeviceExceptionEnum.E_0009.getMessage());
				}
			}
			if (device != null && user != null) {
				if (operatorIds.contains(device.getOperatorId()) && operatorIds.contains(user.getOperatorId())) {
					logger.info("valid pass.");
				}else{
					logger.error("active device failed.operatorIds:{},device operatorId:{}, user operatorId:{}",
							JSONObject.toJSONString(operatorIds), device.getOperatorId(), user.getOperatorId());
					throw new NbiotException(OperatorExceptionEnum.E_00011.getCode(), OperatorExceptionEnum.E_00011.getMessage());
				}
			}
			else {
				logger.error("parameter wrong.device:{}, user:{}", JSONObject.toJSONString(device), JSONObject.toJSONString(user));
				throw new NbiotException(400, "");
			}
		}
	}

	/**
	 * 验证设备和安装点运营公司是否一致
	 */
	private boolean validInstallSiteAndDeviceOperatorId(List<Integer> operatorIds, Integer installSiteId) {
		TinstallSite installSite = installSiteService.findById(installSiteId);
		if (installSite != null && operatorIds.contains(installSite.getOperatorId())) {
			logger.info("valid pass.");
			return true;
		}
		else if (installSite != null) {
			logger.error("valid failed.installSite operator id is not equls user's operator id,operatorIds:{}, installSite operatorId:{}", JSONObject.toJSONString(operatorIds), installSite.getOperatorId());
			throw new NbiotException(OperatorExceptionEnum.E_00014.getCode(), OperatorExceptionEnum.E_00014.getMessage());
		}
		return false;
	}

	/**
	 * 根据传入的类型获取spring管理的对应dao
	 */
	private <T> T getDAO(Class<T> clazz, HttpServletRequest request){
		BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
		return factory.getBean(clazz);
	}

}
