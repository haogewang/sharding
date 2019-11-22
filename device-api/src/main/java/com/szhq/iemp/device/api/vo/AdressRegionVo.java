package com.szhq.iemp.device.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 未分配设备安装运营商统计
 *
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdressRegionVo implements Serializable {


    private static final long serialVersionUID = 1L;
    private Long id;
    private String areaName;
    private String areaCode;
    private Long parentId;
    private String parentAreaName;
    private String parentAreaCode;

}
