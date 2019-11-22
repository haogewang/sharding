package com.szhq.iemp.device.api.vo.query;


import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class RegionQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private Integer regionId;

    private List<Integer> regionIds;

}
