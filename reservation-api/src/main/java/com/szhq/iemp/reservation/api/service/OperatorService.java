package com.szhq.iemp.reservation.api.service;


import com.szhq.iemp.reservation.api.model.Toperator;

import java.util.List;

public interface OperatorService {

	/**
	 * 递归找到所有子类Id
	 */
	List<Integer> findAllChildIds(Integer parentId);

	/**
	 * 根据id查找
	 */
	Toperator findById(Integer id);
}
