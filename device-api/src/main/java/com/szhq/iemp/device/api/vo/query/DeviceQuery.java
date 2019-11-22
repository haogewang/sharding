package com.szhq.iemp.device.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 设备过滤条件
 * @author wanghao
 */
@Data
@ToString(callSuper = true)
public class DeviceQuery extends BaseQuery {

	private static final long serialVersionUID = 1L;
	/**
	 * 设备号
	 */
	private String imei;
	/**
	 * 箱号
	 */
	private String boxNumber;
	/**
	 * 车牌号
	 */
	private String plateNumber;
	/**
	 * 设备状态
	 */
	private String devstate;
	/**
	 * 设备类型
	 */
	private String devType;
	/**
	 * 安装点名称
	 */
	private String installSiteName;
	/**
	 * 是否分配
	 */
	private Boolean isDispache;
	/**
	 * 入库、退库类型
	 */
	private String type;
	/**
	 * 入库时间
	 */
	private Date putStorageTime;
	/**
	 * 是否入库 1:未入库
	 */
	private Integer isPutStorage;
	/**
	 * 分配时间
	 */
	private Date dispacheTime;
	/**
	 * 设备分组Id
	 */
	private Integer groupId;
	/**
	 * 仓库名
	 */
	private String storehouseName;
	/**
	 * 运营公司名称
	 */
	private String operatorName;
	/**
	 * 是否安装点模块列表
	 */
	private Boolean isInstallSite = false;
}
