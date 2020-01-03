package com.szhq.iemp.reservation.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.model.BaseEntity;
import com.szhq.iemp.common.resolver.DesensitizedAnnotation;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Toperator;
import com.szhq.iemp.reservation.api.model.Tuser;
import lombok.Data;

import javax.persistence.Transient;

/**
 * @author wanghao
 * @date 2020/1/3
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegisterVo extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private Long registerId;

    private String imei;

    private Integer operatorId;

    private String userId;

    private Long electrmobileId;

    private String plateNumber;

    private String username;

    private String modelNo;

    private String installSiteName;

    private Integer installSiteId;

    private String isp;

    private String payNumber;


    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String phone;

    @DesensitizedAnnotation(type = TypeEnum.ID_NUMBER)
    private String idNumber;

    @DesensitizedAnnotation(type = TypeEnum.HOME)
    private String home;

    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String contactPhone;

    @Transient
    private Tuser user;

    @Transient
    private Telectrmobile electrombile;
    /**
     * 预约号
     */
    @Transient
    private String reservationNo;

    @Transient
    private Toperator operator;

    @Transient
    private Long registrationId;
}
