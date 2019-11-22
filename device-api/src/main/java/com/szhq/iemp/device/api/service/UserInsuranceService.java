package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TuserInsurance;

import java.util.List;

public interface UserInsuranceService {

    List<TuserInsurance> findByPolicyId(Long policyId);
}
