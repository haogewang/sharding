package com.szhq.iemp.device.api.vo.query;


import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class InstallSiteQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private Boolean status;

    private Integer installSiteId;

    private String installSiteName;

    private String policeName;

    private String policeId;

    private Integer addressRegionId;

    private Double onLineRate;

    private List<Integer> regionIds;
    /**
     * 是否统计
     */
    private Boolean isStatistics = false;

    private Integer offset = 7;

    private Integer provinceId;

    private Integer cityId;
}
