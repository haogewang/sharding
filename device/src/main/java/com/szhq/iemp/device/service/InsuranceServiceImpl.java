package com.szhq.iemp.device.service;

import com.szhq.iemp.device.api.model.Tinsurance;
import com.szhq.iemp.device.api.service.InsuranceService;
import com.szhq.iemp.device.api.vo.PolicyName;
import com.szhq.iemp.device.api.vo.enums.PolicyNameEnum;
import com.szhq.iemp.device.api.vo.enums.PolicyTypeEnum;
import com.szhq.iemp.device.repository.InsuranceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class InsuranceServiceImpl implements InsuranceService {

	@Resource
	private InsuranceRepository insuranceRepository;


	@Override
	public List<Tinsurance> list() {
		return insuranceRepository.findAll();
	}

	@Override
	public Page<Tinsurance> page(int page,int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
		return insuranceRepository.findAll(pageable);
	}

	@Override
	public Tinsurance add(Tinsurance entity) {
		return insuranceRepository.save(entity);
	}

	@Override
	public Integer delete(Integer id) {
		return insuranceRepository.deleteByInsuranceId(id);
	}

	@Override
	public Tinsurance findById(Integer id) {
		return insuranceRepository.findById(id).orElse(null);
	}

	@Override
	public List<Tinsurance> findByIdIn(List<Integer> ids) {
		List<Tinsurance> result = insuranceRepository.findByIdIn(ids);
		return result;
	}

	@Override
	public List<PolicyName> getAllNames() {
		List<PolicyName> maps = new ArrayList<>();
		List<Map<String, Object>> lists = insuranceRepository.getAllNames();
		if(lists != null && !lists.isEmpty()){
			for(Map<String, Object> map : lists){
				Integer code = Integer.valueOf((String.valueOf(map.get("name_code"))));
				String name = String.valueOf(map.get("name"));
				PolicyName policyName = new PolicyName();
				policyName.setCode(code);
				policyName.setName(name);
				maps.add(policyName);
			}
		}
		return maps;
	}

	@Override
	public Map<Integer, String> getAllTypes() {
		Map<Integer, String> map = new HashMap<>();
		for (PolicyTypeEnum policyEnum : PolicyTypeEnum.values()) {
			map.put(policyEnum.getCode(), policyEnum.getType());
		}
		return map;
	}

	@Override
	public String getNameByCode(Integer code) {
		return PolicyNameEnum.getNameByCode(code);
	}

	@Override
	public String getTypeByCode(Integer code) {
		return PolicyTypeEnum.getTypeByCode(code);
	}

	@Override
	public List<Tinsurance> listByPolicyCode(Integer id) {
		return insuranceRepository.listByPolicyCode(id);
	}

}
