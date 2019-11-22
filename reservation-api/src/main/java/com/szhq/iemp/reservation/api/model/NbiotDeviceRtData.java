package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 实时数据表
 * @author wanghao
 */
@Entity(name = "nbiot_device_rt_data")
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NbiotDeviceRtData extends BaseEntity {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceDataId;

    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String imei;

    private Long ts;

    private String gps;

    private Integer operatorId;

    private Integer alert;

    private Integer temperature;

    private String acceleration;

    private Integer voltage;

    private Integer accStatus;

    private String batteryLevel;

    private Integer engSpeed;

    private Integer carSpeed;

    private Integer wtmp;

    private Integer gasLeval;

    private Integer engLoad;

    private Integer map;

    private Integer iat;

    private Integer iaf;

    private Integer absPos;

    private Integer fuePer;

    private Integer timeStart;

    private Integer dtcMil;

    private Integer vol;

    private Integer acc;

    private String netType;

    private String type;

    private Integer sigBar;

    private Integer precisions;

    private Long obdTime;

    private Integer positionMarker;

    private Integer visibleSatellitesNumber;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String wlanInfo;

    @Transient
    private String deviceName;
    @Transient
    private Integer frequency;
}