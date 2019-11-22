package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TelectrombileUser;
import jnr.ffi.Struct;

import java.util.List;

public interface ElecmobileUserService {
	/**
	 * 根据userId查找elec_user关系
	 */
	List<TelectrombileUser> findByUserId(String userId);

	/**
	 * 根据userId/运营公司Id查找elec_user关系
	 */
	List<TelectrombileUser> findByUserIdAndOperatorIds(String userId, List<Integer> operatorIds);
	/**
	 * 根据userId找NoTrackerElecId
	 */
	List<Long> findNoTrackerElecIdByUserId(String userId);
	/**
	 * 根据elecId查找elec_user关系
	 */
	List<TelectrombileUser> findByElecId(Long elecId);
	/**
	 * 根据noTrackerElecId查找elec_user关系
	 */
	TelectrombileUser findByNoTrackerElecId(Long noTrackerElecId);
	/**
	 * 保存
	 */
	TelectrombileUser save(TelectrombileUser entity);
	/**
	 * 修改
	 */
	Long update(TelectrombileUser entity);
	/**
	 * 根据userId删除elec_user关系
	 */
	Integer deleteByUserId(String userId);
	/**
	 * 根据elecId删除elec_user关系
	 */
	Integer deleteByElecId(Long elecId);

	/**
	 * 根据noTrackerElecId删除elec_user关系
	 */
	Integer deleteByNoTrackerElecId(Long noTrackerElecId);

	/**
	 * 包含elecId的值
	 */
	List<String> findByElecIdIn(List<Long> ids);
}
