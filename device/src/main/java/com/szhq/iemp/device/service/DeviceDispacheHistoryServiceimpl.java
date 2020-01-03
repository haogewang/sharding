package com.szhq.iemp.device.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceDispatchHistory;
import com.szhq.iemp.device.api.service.DeviceDispacheHistoryService;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.repository.DeviceDispachHistoryRepository;
import com.szhq.iemp.device.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@CacheConfig(cacheNames = "aepDispacheHistoryLog")
public class DeviceDispacheHistoryServiceImpl implements DeviceDispacheHistoryService {

	@Resource
	private DeviceDispachHistoryRepository dispatchHistoryRepository;

	@Autowired
	private RedisUtil redisUtil;

	@Cacheable(unless="#result == null|| #result.getTotal() == 0")
	@Override
	public MyPage<TdeviceDispatchHistory> findAllByCriteria(Integer page, Integer size, String sorts, String orders, DeviceQuery deviceQuery) {
		Sort sort = SortUtil.sort(sorts, orders, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<TdeviceDispatchHistory> pages = dispatchHistoryRepository.findAll(new Specification<TdeviceDispatchHistory>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<TdeviceDispatchHistory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(deviceQuery != null) {
					if(null != deviceQuery.getDispacheTime()){
						LocalDate localDate = deviceQuery.getDispacheTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						localDate = localDate.plusDays(1);
						Date endDate = new Date(java.sql.Date.valueOf(localDate).getTime() - 1L);
						list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), deviceQuery.getDispacheTime(), endDate));
					}
					if(null != deviceQuery.getStartTime() && deviceQuery.getEndTime() != null){
						list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), deviceQuery.getStartTime(), deviceQuery.getEndTime()));
					}
					if(StringUtils.isNotEmpty(deviceQuery.getBoxNumber())) {
						list.add(criteriaBuilder.equal(root.get("boxNumber").as(String.class), deviceQuery.getBoxNumber()));
					}
					if(StringUtils.isNotEmpty(deviceQuery.getImei())) {
						list.add(criteriaBuilder.equal(root.get("imei").as(String.class), deviceQuery.getImei()));
					}
					if(StringUtils.isNotEmpty(deviceQuery.getType())) {
						list.add(criteriaBuilder.equal(root.get("type").as(String.class), deviceQuery.getType()));
					}
					if(deviceQuery.getOperatorId() != null) {
						list.add(criteriaBuilder.equal(root.get("operatorId").as(Integer.class), deviceQuery.getOperatorId()));
					}
					if(deviceQuery.getOperatorIdList() != null) {
						list.add(root.get("operatorId").as(Integer.class).in(deviceQuery.getOperatorIdList()));
					}
					if(StringUtils.isNotEmpty(deviceQuery.getInstallSiteName())) {
						Expression<String> installSiteOldEx = root.get("oldsiteName").as(String.class);
						Predicate p1 = criteriaBuilder.like(installSiteOldEx, "%" + deviceQuery.getInstallSiteName() + "%");
						list.add(criteriaBuilder.or(p1));
					}
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		return new MyPage<TdeviceDispatchHistory>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
	}

	@Override
	public Integer deleteById(Integer id) {
		return dispatchHistoryRepository.deleteByHistoryId(id);
	}

	@Override
	public TdeviceDispatchHistory findByImei(String imei) {
		return dispatchHistoryRepository.findByImei(imei);
	}

	@Override
	public TdeviceDispatchHistory save(TdeviceDispatchHistory entity) {
		return dispatchHistoryRepository.save(entity);
	}

	@Override
	public List<TdeviceDispatchHistory> saveAll(List<TdeviceDispatchHistory> list) {
		return dispatchHistoryRepository.saveAll(list);
	}

	@Override
	public Integer deleteDisPatchRedis() {
		int count = 0;
		if(redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN) != null && redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN).size() > 0) {
			Set<String> sets = redisUtil.keys(CommonConstant.HISTORY_DISPACHE_LOG_PATTERN);
			for(String key : sets) {
				redisUtil.del(key);
				count ++;
			}
		}
		return count;
	}

}
