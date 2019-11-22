package com.szhq.iemp.device.api.vo.query;


import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class PolicyQuery extends BaseQuery {

	private static final long serialVersionUID = 1L;
	
	private Date startTime;
	private Date endTime;
	private String imei;

}
