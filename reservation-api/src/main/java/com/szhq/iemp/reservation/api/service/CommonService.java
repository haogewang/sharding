package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TcommonConfig;

public interface CommonService {

    TcommonConfig findByName(String name);

    /**
     * 修改value的值
     */
    Integer setByName(String name, String value);

}
