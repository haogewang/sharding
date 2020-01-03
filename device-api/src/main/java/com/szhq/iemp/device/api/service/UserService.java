package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.Tuser;

import java.util.List;

public interface UserService {

    Tuser findById(String id);

    List<Tuser> findBySiteId(Integer siteId);

}
