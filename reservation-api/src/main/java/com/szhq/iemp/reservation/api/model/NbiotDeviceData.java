package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

/**
 * cassandra历史数据
 */
@Table(value = "nbiot_history_data")
@lombok.Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NbiotDeviceData implements Serializable {

    @PrimaryKey
    private NbiotTrackerDataPK trackerDataPK;

    private String gps;

    private String temperature;

    private String acceleration;

    private int voltage;

    @Column(value = "acc_status")
    private int accStatus;

    private int alert;

    @Column(value = "create_time")
    private String createTime;

    @Column(value = "battery_level")
    private String batteryLevel;

    private Integer engSpeed;

    private Float accuVolt;

    private Float deviceVolt;

    private Integer acceX;

    private Integer acceY;

    private Integer acceZ;

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

    private Integer precision;

    private Long obdTime;

    private Integer positionMarker;

    private Integer visibleSatellitesNumber;
    private int operatorId;

    private static final long serialVersionUID = 1L;
}

