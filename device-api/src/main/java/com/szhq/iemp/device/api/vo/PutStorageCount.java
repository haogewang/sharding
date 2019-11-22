package com.szhq.iemp.device.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 入库设备统计
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutStorageCount implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 箱数
     */
    private Long boxCount;
    /**
     * 设备数
     */
    private Long imeiCount;
    /**
     * 入库时间
     */
    private Date createTime;
    /**
     * CT数
     */
    private Long ctCount;
    /**
     * CMCC数
     */
    private Long cmccCount;

}
