package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.model.BaseEntity;
import com.szhq.iemp.common.resolver.DesensitizedAnnotation;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.Valid;

@Entity(name = "t_registration")
@Data
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tregistration extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registerId;

    @Column(columnDefinition = "varchar(32) COMMENT '设备imei号'")
    private String imei;

    @Column(columnDefinition = "INT COMMENT '运营公司Id'", nullable = false)
    private Integer operatorId;

    @Column(columnDefinition = "varchar(32) COMMENT '用户Id'", nullable = false)
    private String userId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Column(columnDefinition = "bigint COMMENT '用户Id'", nullable = false)
    private Long electrmobileId;

    @Column(columnDefinition = "varchar(32) COMMENT '车牌号'", nullable = false, unique = true)
    private String plateNumber;

    @Column(columnDefinition = "varchar(32) COMMENT '车主姓名'")
    private String username;

//    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    @Column(columnDefinition = "varchar(32) COMMENT '登录账号'")
    private String phone;

//    @DesensitizedAnnotation(type = TypeEnum.ID_NUMBER)
    @Column(columnDefinition = "varchar(40) COMMENT '身份证号'")
    private String idNumber;

    @Column(columnDefinition = "varchar(40) COMMENT '安装点名称'")
    private String installSiteName;

    @Column(columnDefinition = "INT COMMENT '安装点Id'")
    private Integer installSiteId;

    @Column(columnDefinition = "varchar(20) COMMENT '设备所属运营商'")
    private String isp;

    @Column(columnDefinition = "varchar(40) COMMENT '支付订单号'")
    private String payNumber;

    private String modelNo;

//    @Valid
    @Transient
    private Tuser user;

//    @Valid
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