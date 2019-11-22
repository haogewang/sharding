package com.szhq.iemp.device.api.vo.query;


import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class GroupQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private String groupName;

    private Integer groupId;

    private String imei;
    //组类型（1：设备分组 2：车辆分组）
    private Integer type;

    //如:大屏显示
    private Integer customType;

    private String plateNo;


}
