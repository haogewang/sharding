package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TelectrombileUser;

import java.util.List;

public interface ElecmobileUserService {
    /**
     * 根据userId查找elec_user关系
     */
     List<TelectrombileUser> findByUserId(String userId);

    /**
     * 保存
     */
     TelectrombileUser save(TelectrombileUser entity);


}
