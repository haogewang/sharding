package com.szhq.iemp.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@MappedSuperclass
@ToString
public class BaseQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 无线服务商Id
     */
    protected Integer iotTypeId;
    /**
     * 运营公司Id
     */
    protected Integer operatorId;
    /**
     * 运营公司Id集合
     */
    protected List<Integer> operatorIdList;
    /**
     * 归属地Id
     */
    protected Integer regionId;
    /**
     * 仓库Id
     */
    protected Integer storehouseId;
    /**
     * 所有人Id
     */
    protected String ownerId;
    /**
     * 安装点id
     */
    protected Integer installSiteId;
    /**
     * 设备厂商Id
     */
    protected Integer manufactorId;
    /**
     * 运营商（CMCC/CT）
     */
    private String isp;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
