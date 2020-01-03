package com.szhq.iemp.device.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVo implements Serializable {


    private static final long serialVersionUID = 1L;

    private String imei;

    private String isp;

    private String operatorName;

    private String storehouseName;

    private Boolean isHavePolicy;

    private Integer policyNameCode;

    private String policyName;

}
