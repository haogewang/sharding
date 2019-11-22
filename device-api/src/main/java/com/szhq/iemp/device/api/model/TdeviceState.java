package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备状态
 */
@Entity(name = "nbiot_state")
@Data
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String imei;

    private String devType;

    private String version;

    private Integer devMode;

    private Integer uint;

    private Integer devCode;

    private Integer hostStat;

    private Integer motionStat;

    private Double deviceVolt;

    private Integer deviceVoltper;

    private BigDecimal temp;

    private Integer signNum;
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    private String signTime;

    private Integer fenNum;

    private String fenIds;

    private Long stateTime;

    private Long otherTime;

    private String fwVersion;

    @Transient
    private String function;

}
