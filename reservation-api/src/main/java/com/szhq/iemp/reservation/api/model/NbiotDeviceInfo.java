package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wanghao
 * @date 2019/8/8
 */
@Entity(name="nbiot_device_info")
@Data
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NbiotDeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer infoId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * imei号
     */
    private String imei;

    /**
     * 设备状态
     */
    private Byte status;

    /**
     * 设备状态
     */
    private Byte localStatus;

    /**
     * 上线时间
     */
    private Date onlineTime;

    /**
     * 下线时间
     */
    private Date offlineTime;

    /**
     * 告警类型
     */
    private Integer alert;

    /**
     * 上一次上数时间
     */
    private Date lastDataTime;

    /**
     *无线服务商Id
     */
    private int iotTypeId;

    /**
     *仓库Id
     */
    private int deviceStorehouseId;

    /**
     * 安装点id
     */
    private int installSiteId;

    /**
     * 运营公司id
     */
    private int operatorId;

    /**
     * 归属地id
     */
    private int residentId;
}
