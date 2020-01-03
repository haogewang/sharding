package com.szhq.iemp.reservation.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ElecmobileQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private String devname;
    /**
     * 车牌号(模糊搜索)
     */
    private String plateNo;
    /**
     * 精确查找车牌
     */
    private String plateNumber;
    /**
     * 车主姓名
     */
    private String ownerName;
    /**
     * 索引
     */
    private String index;

    private String imei;

    private Integer type;

    private String phone;

    private String modelNo;

    private String userId;
}
