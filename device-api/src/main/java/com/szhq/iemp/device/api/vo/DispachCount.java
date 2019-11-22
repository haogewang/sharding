package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 分配统计
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DispachCount implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 设备总数
     */
    private Long totalCount;
    /**
     * CT数
     */
    private Long ctCount;
    /**
     * CMCC数
     */
    private Long cmccCount;
    /**
     * CUCC数
     */
    private Long cuccCount;
    /**
     * 分配时间
     */
    private Date dispatchTime;
    /**
     * 安装点名称
     */
    private String installSiteName;
    /**
     * 安装点Id
     */
    private Long installSiteId;

}
