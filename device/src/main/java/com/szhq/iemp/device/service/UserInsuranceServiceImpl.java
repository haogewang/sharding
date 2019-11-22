package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.TuserInsurance;
import com.szhq.iemp.device.api.service.UserInsuranceService;
import com.szhq.iemp.device.repository.UserInsuranceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserInsuranceServiceImpl implements UserInsuranceService {

	@Resource
	private UserInsuranceRepository userInsuranceRepository;

	@Override
	public List<TuserInsurance> findByPolicyId(Long policyId) {
		return userInsuranceRepository.findByPolicyId(policyId);
	}
}
