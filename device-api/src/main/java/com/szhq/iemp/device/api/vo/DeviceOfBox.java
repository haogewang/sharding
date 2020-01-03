package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 箱号下imei及isp
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceOfBox implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 设备号
     */
    private String imei;
    /**
     * 运营商
     */
    private String isp;
    /**
     * 状态
     */
    private Long status;
    /**
     * 仓库id
     */
    private Long storeHouseId;
    /**
     * 仓库名
     */
    private String storeHouseName;
    /**
     * 箱号
     */
    private String boxNumber;
    /**
     * 箱子设备数
     */
    private Long boxDeviceNumber;
    /**
     * 入库人员名称
     */
    private String putStorageUserName;
}
