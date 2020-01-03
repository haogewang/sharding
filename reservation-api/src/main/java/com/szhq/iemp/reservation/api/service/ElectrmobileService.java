package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.vo.TelectrmobileVo;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ElectrmobileService {

	/**
	 * 模糊查询
	 */
	MyPage<Telectrmobile> findElecByCriteria(Integer offset, Integer limit, String sort, String order, ElecmobileQuery elecQuery);

	/**
	 * 根据车牌号查找电动车
	 */
	Telectrmobile findByPlateNumber(String plateNumber);
	/**
	 * 根据imei查找电动车
	 */
	Telectrmobile findByImei(String imei);
	/**
	 * 根据deviceId查找电动车信息
	 */
	Telectrmobile findByIotDeviceId(String deviceId);

	/**
	 * 根据id删除电动车
	 */
	Integer deleteByElecId(Long id);

	/**
	 * 保存电动车
	 */
	Telectrmobile save(Telectrmobile electrombile);
	/**
	 * 获取布防状态
	 */
	String getBfStatus(String terminalId);
	/**
	 * 根据Id查找
	 */
	Telectrmobile findByElecId(Long id);
	/**
	 * 修改布防状态
	 */
	Integer updateBfStatus(String imei, boolean mode);
	/**
	 * 修改布控状态
	 */
	Integer updateBkStatus(String imei, boolean mode);
	/**
	 * 根据imei删除电动车
	 */
	void deleteByImei(String imei);

	/**
	 * 根据车架号查找电动车
	 */
	List<Telectrmobile> findByVin(String vin);
	/**
	 * 根据电机号查找电动车
	 */
	List<Telectrmobile> findByMotorNumber(String motorNumber);

	/**
	 * 创建电动车
	 */
	Telectrmobile createElectrombile(Telectrmobile electrombile);
	/**
	 * 获取所有电动车厂商
	 */
	List<TelectrombileVendor> getElecmobileVendors();
	/**
	 * 获取所有电动车颜色
	 */
	List<TelectrombileColor> getElecmobileColors();
	/**
	 * 获取所有电动车类型
	 */
	List<TelectrombileType> getElecmobileTypes();
	/**
	 * 通过colorId查找color
	 */
	TelectrombileColor findByColorId(Integer id);
	/**
	 * 通过vendorId查找Vendor
	 */
	TelectrombileVendor findByVendorId(Integer id);
	/**
	 * 通过typeId查找type
	 */
	TelectrombileType findByTypeId(Integer id);

	/**
	 * 查询今天安装已上线的设备数量
	 */
	Long countTodayInstalledEquip(Integer installSiteId);

	/**
	 * 根据userId查找所有电动车
	 */
	List<Telectrmobile> getAllElecmobileByUserId(String userId);

	/**
	 * 根据索引查找vendor
	 */
	List<TelectrombileVendor> getTypeByIndex(String index);
	/**
	 * 添加颜色
	 */
	TelectrombileColor addColors(TelectrombileColor entity);
	/**
	 * 添加品牌
	 */
	TelectrombileVendor addVendors(TelectrombileVendor entity);
	/**
	 * 添加类型
	 */
	TelectrombileType addTypes(TelectrombileType entity);

	Integer deleteElecColorById(Integer id);

	Integer deleteElecVendorById(Integer id);

	Integer deleteElecTypeById(Integer id);
	/**
	 * 删除redis缓存
	 */
	Integer deleteElecRedisData();
	/**
	 * 删除redis颜色，类型，品牌
	 */
	Integer deleteRedisColorTypeVendor();

	/**
	 * 根据imeis获取车牌号
	 */
	Map<String, String> getPlateNoByImeis(List<String> imeis);

	/**
	 * 给设备号设置名称
	 */
	void setNameByImei(String imei, String name);
	/**
	 * 设置设备频率
	 */
	void setFrequencyByImei(String imei, Integer frequency);
	/**
	 * 设置查看告警时间
	 */
	void setViewDateByImei(String imei, Date date);

	void setViewDateByImeis(List<String> imeis, Date date);

	/**
	 * 通过用户Id查找所有电动车(包括未绑定设备车)
	 */
	List<Telectrmobile> findAllElecByUserId(String userId, String type, List<Integer> operatorIds, Boolean isApp);
	/**
	 * 根据车牌号查询车及用户信息
	 */
	Telectrmobile getElecAndUserInfoByPlateNo(ElecmobileQuery elecQuery);

	/**
	 * 根据userId及设备类型获取所有绑定设备电动车
	 */
	List<Telectrmobile> getAllElecmobileByUserIdAndType(String userId, String type);

	/**
	 * 根据imei修改保单号
	 */
	Integer updatePolicyNoByImei(String policyNo, String imei);

	/**
	 * 批量导入excel修改保单号
	 */
	Integer batchImportExcelUpdatePolicyNo(MultipartFile file);


}
