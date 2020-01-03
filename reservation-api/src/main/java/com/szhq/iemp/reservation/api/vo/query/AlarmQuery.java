package com.szhq.iemp.reservation.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class AlarmQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private String plateNo;

    private String phone;

    private String ownerName;

    private String imei;

    private Long startTimestamp;

    private Long endTimestamp;

    private Integer type;

    private boolean app = false;
    /**
     * 产品类型(302/310)
     */
    private String modelNo;

    private List<String> imeiList;

    private Integer emergencyAlert;
}
