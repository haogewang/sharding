package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 激活设备数量统计
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActiveDeviceCount implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalCount;

    private Long activeCount;

    private Long noActiveCount;

    private Integer storehouseId;

    private String storehouseName;

    private String operatorName;

    private String groupId;

    private String groupName;

    private Date date;


}
