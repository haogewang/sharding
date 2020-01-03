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
	 * 根据elecId查找elec_user关系
	 */
	List<TelectrombileUser> findByElecId(Long elecId);
	/**
	 * 保存
	 */
	TelectrombileUser save(TelectrombileUser entity);
	/**
	 * 修改
	 */
	Long update(TelectrombileUser entity);
	/**
	 * 根据userId删除elecUser关系
	 */
	Integer deleteByUserId(String userId);

	/**
	 * 根据elecId删除elecUser关系
	 */
	Integer deleteByElecId(Long elecId);


    TelectrombileUser findByUserIdAndElecId(String userId, Long elecId);
}
