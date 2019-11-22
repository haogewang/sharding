package com.szhq.iemp.device.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 按照运营商统计未分配设备
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnDispacheDeviceCount implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 运营商
	 */
	private String isp;
	/**
	 * 数量
	 */
	private Long count;
	
	
}
