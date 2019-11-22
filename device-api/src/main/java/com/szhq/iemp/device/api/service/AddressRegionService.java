package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TaddressRegion;
import com.szhq.iemp.device.api.vo.AdressRegionVo;
import com.szhq.iemp.device.api.vo.query.RegionQuery;

import java.util.List;
import java.util.Map;

public interface AddressRegionService {

	/**
	 * 列表
	 */
	MyPage<TaddressRegion> findByCretira(Integer offset, Integer limit, String sort, String order, RegionQuery query);
	/**
	 * 通过名称查找
	 */
	List<TaddressRegion> findByNameLike(String name);
	/**
	 * 通过id查找
	 */
	TaddressRegion findById(Integer id);

	List<TaddressRegion> findByIds(List<Integer> ids);
	/**
	 * 找出下一级所有子类
	 */
	List<AdressRegionVo> findNexLevelAllChildrenById(Integer id);
	/**
	 * 根据ids获取下一级所有子类
	 */
	Map<Integer, List<AdressRegionVo>> getNexLevelAllChildrenByIds(List<Integer> ids);

	/**
	 * 获取所有安装点的区域
	 */
	List<TaddressRegion> getAllSiteCities();

	/**
	 *根据cityId获取所有区域
	 */
	List<TaddressRegion> getAllRegionsByCityId(Integer id);

	Integer deleteRegionRedisData();
}
