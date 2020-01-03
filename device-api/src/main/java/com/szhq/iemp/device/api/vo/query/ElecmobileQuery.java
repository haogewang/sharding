package com.szhq.iemp.device.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class ElecmobileQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;
    /**
     * 车牌号(模糊搜索)
     */
    private String plateNo;
    /**
     * 分组Id
     */
    private Integer groupId;

    private List<Integer> groupIdList;

}
