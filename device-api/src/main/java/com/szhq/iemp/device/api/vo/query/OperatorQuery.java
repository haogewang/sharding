package com.szhq.iemp.device.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class OperatorQuery extends BaseQuery {

	private static final long serialVersionUID = 1L;
	/**
	 * 运营公司名称
	 */
	private String operatorName;
	/**
	 * 运营公司父Id
	 */
	private Integer parentId;
}
