package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 备案预约
 * @author wanghao
 */
@Entity(name = "t_reservation")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Treservation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;
    /**
     * 是否购买设备
     */
    private Boolean isPurchase = false;
    /**
     * 是否本人
     */
    private Boolean isSelf = true;
    /**
     * 安装点id
     */
    private Integer installSiteId;

    private String installSiteName;

    private Long reservationTime;

    @Column(columnDefinition = "varchar(32)")
    private String phone;

    @Column(columnDefinition = "varchar(32)")
    private String reservationNumber;

    @Column
    private Integer vendorId;

    @Column
    private Integer colorId;

    private Integer typeId;

    @Column(columnDefinition = "varchar(32)")
    private String plateNumber;

    @Column(columnDefinition = "varchar(32)")
    private String vin;

    @Column(columnDefinition = "varchar(32)")
    private String ownerName;

    private Byte sex;

    private Integer age;

    @Column(columnDefinition = "varchar(100)")
    private String birthPlace;

    @Column(columnDefinition = "varchar(32)")
    private String provinceNo;

    @Column(columnDefinition = "varchar(50)")
    private String province;

    @Column(columnDefinition = "varchar(100)")
    private String home;

    private Byte idType;

    @Column(columnDefinition = "varchar(32) COMMENT '证件号'")
    private String idNumber;

    private Date purchaseTime;
    /**
     * 电机号
     */
    @Column(columnDefinition = "varchar(32) COMMENT '电机号'")
    private String motorNumber;

    @Column(columnDefinition = "varchar(400) COMMENT '电动车照片'")
    private String motorPhotoUrl;

    @Column(columnDefinition = "varchar(400) COMMENT '身份证正面照片'")
    private String idNumberPhotoUrl;

    @Column(columnDefinition = "varchar(400) COMMENT '身份证反面照片'")
    private String idNumberPhotoBackUrl;

    @Column(columnDefinition = "varchar(400) COMMENT '签名照片'")
    private String signPhotoUrl;
    /**
     * 代办人名称
     */
    private String agentName;
    /**
     *  代办人身份证号
     */
    private String agentIdNumber;
    @Transient
    private String imei;
    @Transient
    private Integer elecVendorId;
    @Transient
    private Integer elecColorId;
    @Transient
    private Integer elecTypeId;



}