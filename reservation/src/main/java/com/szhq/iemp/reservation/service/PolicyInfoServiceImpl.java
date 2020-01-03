package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TpolicyInfo;
import com.szhq.iemp.reservation.api.service.PolicyInfoService;
import com.szhq.iemp.reservation.repository.PolicyInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PolicyInfoServiceImpl implements PolicyInfoService {

	@Resource
	private PolicyInfoRepository policyInfoRepository;

	@Override
	public Long save(TpolicyInfo entity) {
		TpolicyInfo policyInfo = policyInfoRepository.save(entity);
		return policyInfo.getId();
	}

	@Override
	public List<TpolicyInfo> saveAll(List<TpolicyInfo> entities) {
		return policyInfoRepository.saveAll(entities);
	}

	@Override
	public TpolicyInfo findByImeiAndUserId(String imei, String userId) {
		return policyInfoRepository.findByImeiAndUserId(imei, userId);
	}

	@Override
	public TpolicyInfo findByImeiAndUserIdisNull(String imei) {
		return policyInfoRepository.findByImeiAndUserIdisNull(imei);
	}

	@Override
	public TpolicyInfo findByImei(String imei) {
		return policyInfoRepository.findByImei(imei);
	}


}
