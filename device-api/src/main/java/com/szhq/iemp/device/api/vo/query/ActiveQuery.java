package com.szhq.iemp.device.api.vo.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.vo.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wanghao
 * @date 2019/10/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActiveQuery extends BaseQuery {

    private String activatorId;

    private Integer mode;

    private String groupId;

    private Integer offset;

    //仓库是否激活
    private Boolean isActive = false;

}
