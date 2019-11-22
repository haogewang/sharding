package com.szhq.iemp.common.config;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * mysql Dialect配置
 * @author wanghao
 */
public class MysqlConfig extends MySQL5Dialect{

	@Override
	public String getTableTypeString() {
		return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
	}
}
