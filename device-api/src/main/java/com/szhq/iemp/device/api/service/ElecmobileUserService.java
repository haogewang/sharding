package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TelectrombileUser;

import java.util.List;

public interface ElecmobileUserService {
    /**
     * 根据userId查找elec_user关系
     */
     List<TelectrombileUser> findByUserId(String userId);

    /**
     * 根据userId找NoTrackerElecId
     */
     List<Integer> findNoTrackerElecIdByUserId(String userId);

    /**
     * 根据elecId查找elec_user关系
     */
     List<TelectrombileUser> findByElecId(Integer elecId);

    /**
     * 根据noTrackerElecId查找elec_user关系
     */
    public TelectrombileUser findByNoTrackerElecId(Integer noTrackerElecId);

    /**
     * 保存
     */
    public TelectrombileUser save(TelectrombileUser entity);

    /**
     * 根据userId删除elec_user关系
     */
    public Integer deleteByUserId(String userId);

    /**
     * 根据elecId删除elec_user关系
     */
    public Integer deleteByElecId(Integer elecId);

    /**
     * 根据noTrackerId删除elec_user关系

     */
    public Integer deleteByNoTrackerElecId(Integer noTrackerElecId);

}
