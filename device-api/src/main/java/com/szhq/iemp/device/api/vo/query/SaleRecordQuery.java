package com.szhq.iemp.device.api.vo.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.vo.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanghao
 * @date 2019/10/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SaleRecordQuery extends BaseQuery {

    private Integer operatorId;

    private String groupId;

    private Integer offset = 7;

}
