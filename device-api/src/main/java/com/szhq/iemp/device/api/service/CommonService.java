package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TcommonConfig;

public interface CommonService {

    TcommonConfig findByName(String name);
    /**
     * 修改value的值
     */
    Integer setByName(String name, String value);

    int deleteRedisByKey(String key);

}
