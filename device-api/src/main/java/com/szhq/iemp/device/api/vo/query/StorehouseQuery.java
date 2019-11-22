package com.szhq.iemp.device.api.vo.query;


import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class StorehouseQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;

    private String storehouseName;

}
