package com.szhq.iemp.reservation.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 从属派出所
 * @author wanghao
 */
@Entity(name = "police_precinct")
@Data
public class TpolicePrecinct implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private Date createTime;

    private Date updateTime;

    private String department;

    private String policeOffice;

    private String policeStation;

    private String precinct;

    private String departmentId;

    private String policeStationId;

    private String policeOfficeId;

    private String precinctId;

    private String policeAddress;


}
