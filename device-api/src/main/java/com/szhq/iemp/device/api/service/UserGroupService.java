package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TuserGroup;

import java.util.List;

public interface UserGroupService {


    TuserGroup findById(String id);

    List<TuserGroup> findByOperatorIdIn(List<Integer> operatorIds);

}
