package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.UserExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.*;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.ActiveQuery;
import com.szhq.iemp.device.api.vo.query.SaleRecordQuery;
import com.szhq.iemp.device.repository.ActiveInfoRepository;
import com.szhq.iemp.device.repository.UserGroupRRepository;
import com.szhq.iemp.device.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ActiveInfoServiceImpl implements ActiveInfoService {

	@Resource
	private ActiveInfoRepository activeInfoRepository;
	@Resource
	private UserGroupRRepository userGroupRRepository;

	@Autowired
	private DeviceInventoryService deviceInventoryService;
	@Autowired
	private ElectrmobileService electrombileService;
	@Autowired
	private ElecmobileUserService elecmobileUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private PolicyInfoService policyInfoService;
	@Autowired
	private SaleRecordService saleRecordService;
	@Autowired
	private OperatorService operatorService;
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public Integer activeImei(String imei, String userId) {
		updateActiveData(imei, userId, 1);
		Integer i = deviceInventoryService.updateActiveStateByImei(imei, true);
		redisUtil.del(CommonConstant.DEVICE_IMEI + imei);
		return i;
	}

	@Override
	public Integer countTodayActiveByActivatorId(String activitorId, Integer mode) {
		return activeInfoRepository.countTodayActiveByActivatorId(activitorId, mode);
	}

	@Override
	public MyPage<TactiveInfo> findAllByCriteria(Integer page, Integer size, String sorts, String orders, ActiveQuery myQuery) {
		Sort sort = SortUtil.sort(sorts, orders, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<TactiveInfo> pages = activeInfoRepository.findAll(new Specification<TactiveInfo>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<TactiveInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (myQuery != null) {
					if (myQuery.getActivatorId() != null) {
						list.add(criteriaBuilder.equal(root.get("activatorId").as(String.class), myQuery.getActivatorId()));
					}
					if (myQuery.getMode() != null) {
						list.add(criteriaBuilder.equal(root.get("mode").as(Integer.class), myQuery.getMode()));
					}
					if (myQuery.getStartTime() != null && myQuery.getEndTime() != null) {
						list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), myQuery.getStartTime(), myQuery.getEndTime()));
					}
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		return new MyPage<TactiveInfo>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
	}

	@Override
	public Integer back(String imei, String userId) {
		Integer i = electrombileService.countByImei(imei);
		if(i > 0){
			log.error("device has bound.imei:" + imei);
			throw new NbiotException(400019, DeviceExceptionEnum.E_00034.getMessage());
		}
		TpolicyInfo policyInfo = policyInfoService.findByImei(imei);
		if(policyInfo != null && policyInfo.getIsEffective()){
			log.error("policy has effective.imei:" + imei);
			throw new NbiotException(400020, DeviceExceptionEnum.E_00038.getMessage());
		}
		updateActiveData(imei, userId, 0);
		TpolicyInfo tpolicyInfo = new TpolicyInfo();
		tpolicyInfo.setImei(imei);
		policyInfoService.updatePolicy(tpolicyInfo);
		Integer j = deviceInventoryService.updateActiveStateByImei(imei, false);
		return j;
	}

	@Override
	public List<TactiveInfo> getActiveByUserId(String userId) {
		List<TactiveInfo> result = new ArrayList<>();
		Tuser user  = userService.findById(userId);
		if(user == null){
			log.error("user is not exist.id:" + userId);
			throw new NbiotException(404, "用户不存在");
		}
		List<TelectrombileUser> elecUsers = elecmobileUserService.findByUserId(userId);
		if(elecUsers != null && !elecUsers.isEmpty()){
			List<Long> elecIds =  elecUsers.stream().map(TelectrombileUser::getElectrombileId).collect(Collectors.toList());
			List<String> imeis = electrombileService.findImeisByElecIds(elecIds);
			log.info("imei:" + JSONObject.toJSONString(imeis));
			if(imeis != null && !imeis.isEmpty()){
				result = findByImeis(imeis);
			}
		}
		return result;
	}

	@Override
	public List<TactiveInfo> findByImeis(List<String> imeis) {
		return activeInfoRepository.findByImeis(imeis);
	}

	@Override
	public Integer countActiveByOperatorIds(List<Integer> operatorIds, int mode, Date startTime, Date endTime) {
		Integer count = 0;
		if(startTime == null && endTime ==null){
			count = activeInfoRepository.countActiveByOperatorIds(operatorIds, mode);
		}else{
			count = activeInfoRepository.countActiveByOperatorIds(operatorIds, mode, startTime, endTime);
		}
		return count;
	}

	@Override
	public Integer countActiveByGroupIds(List<String> groupIds, int mode, Date startTime, Date endTime) {
		Integer count = 0;
		if(startTime == null && endTime ==null){
			count = activeInfoRepository.countActiveByGroupIds(groupIds, mode);
		}else{
			count = activeInfoRepository.countActiveByGroupIds(groupIds, mode, startTime, endTime);
		}
		return count;
	}

	@Override
	public List<ActiveDeviceCount> activeStatisticByOperatorId(SaleRecordQuery query) {
		if(query.getOperatorId() == null){
			return null;
		}
		List<ActiveDeviceCount> result = new ArrayList<>();
		List<Integer> ids =  operatorService.findAllChildIds(query.getOperatorId());
		List<Map<String, Object>> activeStatistics = activeInfoRepository.activeStatisticByOperatorId(ids, query.getOffset());
		if(activeStatistics != null && !activeStatistics.isEmpty()){
			for(Map<String, Object> map : activeStatistics){
				ActiveDeviceCount activeCount = new ActiveDeviceCount();
				String days = (String)map.get("days");
				Long activecount = 0L;
				Long unactivecount = 0L;
				if(map.get("active_count") != null){
					activecount = Long.valueOf(map.get("active_count").toString());
				}
				if(map.get("unactive_count") != null){
					unactivecount = Long.valueOf((String)map.get("unactive_count").toString());
				}
				Date date = TimeStampUtil.parseDate(days, "yyyy-MM-dd");
				activeCount.setActiveCount(activecount);
				activeCount.setNoActiveCount(unactivecount);
				activeCount.setDate(date);
				result.add(activeCount);
			}
		}
		return result;
	}

	@Override
	public List<ActiveDeviceCount> activeStatisticByGroupId(SaleRecordQuery query) {
		if(query.getGroupId() == null){
			return null;
		}
		List<ActiveDeviceCount> result = new ArrayList<>();
		List<Map<String, Object>> saleStatistics = activeInfoRepository.activeStatisticByGroupId(query.getGroupId(), query.getOffset());
		if(saleStatistics != null && !saleStatistics.isEmpty()){
			for(Map<String, Object> map : saleStatistics){
				ActiveDeviceCount activeCount = new ActiveDeviceCount();
				String days = (String)map.get("days");
				Long activecount = 0L;
				Long unactivecount = 0L;
				if(map.get("active_count") != null){
					activecount = Long.valueOf(map.get("active_count").toString());
				}
				if(map.get("unactive_count") != null){
					unactivecount = Long.valueOf((String)map.get("unactive_count").toString());
				}
				Date date = TimeStampUtil.parseDate(days, "yyyy-MM-dd");
				activeCount.setActiveCount(activecount);
				activeCount.setNoActiveCount(unactivecount);
				activeCount.setDate(date);
				result.add(activeCount);
			}
		}
		return result;
	}

	@Override
	public Long save(TactiveInfo entity) {
		TactiveInfo activeInfo = activeInfoRepository.save(entity);
		return activeInfo.getId();
	}

	private void updateActiveData(String imei, String userId, int mode) {
		TdeviceInventory device = deviceInventoryService.findByImei(imei);
		if (device == null) {
			log.error("device is not exist.imei:" + imei);
			throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
		}
		if(device.getIsActive() && mode == 1){
			log.error("device already active.imei:" + imei);
			throw new NbiotException(400021, DeviceExceptionEnum.E_00035.getMessage());
		}
		if(!device.getIsActive() && mode == 0){
			log.error("device already unActive.imei:" + imei);
			throw new NbiotException(400022, DeviceExceptionEnum.E_00036.getMessage());
		}
		Tuser user = userService.findById(userId);
		if (user == null) {
			log.error("user is not exist.id:" + userId);
			//UserExceptionEnum.E_0004
			throw new NbiotException(200002, UserExceptionEnum.E_0004.getMessage());
		}
		String groupId = userGroupRRepository.findGroupIdByUserId(userId);
        if(StringUtils.isEmpty(groupId)){
            log.error("groupId is null.userId:" + userId);
            throw new NbiotException(200007, UserExceptionEnum.E_0009.getMessage());
        }
		TactiveInfo activeInfo = new TactiveInfo();
		activeInfo.setActivatorId(userId);
		activeInfo.setActivatorName(user.getName());
		activeInfo.setImei(imei);
		activeInfo.setGroupId(groupId);
		activeInfo.setMode(mode);
		activeInfo.setOperatorId(device.getOperatorId());
		activeInfo.setStorehouseId(device.getStorehouseId());
		save(activeInfo);
//		saveSaleRecord(activeInfo);
	}

	/**
	 * 保存销售记录
	 */
//	private void saveSaleRecord(TactiveInfo activeInfo) {
//		TsaleRecord record = saleRecordService.findByImeiAndMode(activeInfo.getImei(), activeInfo.getMode());
//		if(record ==null){
//			TsaleRecord saleRecord = new TsaleRecord();
//			BeanUtils.copyProperties(activeInfo, saleRecord, PropertyUtil.getNullProperties(activeInfo));
//			saleRecordService.add(saleRecord);
//		}else {
//			BeanUtils.copyProperties(activeInfo, record, PropertyUtil.getNullProperties(activeInfo));
//			saleRecordService.add(record);
//		}
//	}
}
