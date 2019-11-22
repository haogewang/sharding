package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity(name = "t_electrmobile")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Telectrmobile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long electrmobileId;

    @NotEmpty(message = "设备号不能为空")
    @Column(columnDefinition = "varchar(40) COMMENT '设备号'", unique = true, nullable = false)
    private String imei;

    @Column(columnDefinition = "INT COMMENT '车辆品牌Id'")
    private Integer vendorId;

    @Column(columnDefinition = "INT COMMENT '车辆类型Id'")
    private Integer typeId;

    @Column(columnDefinition = "INT COMMENT '车辆颜色Id'")
    private Integer colorId;

    @Column(columnDefinition = "varchar(50) COMMENT '车架号'")
    private String vin;

    @Column(columnDefinition = "datetime COMMENT '购买时间'")
    private Date purchaseTime;

    @NotEmpty(message = "车牌号不能为空")
    @Column(columnDefinition = "varchar(32) COMMENT '车牌号'", nullable = false)
    private String plateNumber;

    @Column(columnDefinition = "varchar(32) COMMENT '保险单号'")
    private String policyNo;

    @Column(columnDefinition = "varchar(32) COMMENT '承保日期'")
    private Date insuranceTime;

    @Column(columnDefinition = "varchar(300) COMMENT '电动车照片路径'")
    private String motorPhotoUrl;

    @Column(columnDefinition = "varchar(300) COMMENT '承诺书照片路径'")
    private String promisePhotoUrl;

    @Column(columnDefinition = "varchar(300) COMMENT '保险单照片路径'")
    private String policyPhotoUrl;

    @Column(columnDefinition = "varchar(20) COMMENT '保险公司名称'")
    private String policyName;

    @Column(columnDefinition = "varchar(32) COMMENT '电机号'")
    private String motorNumber;

    @Column(columnDefinition = "varchar(10) COMMENT '布控状态'")
    private String embileBkState;

    @Column(columnDefinition = "varchar(10) COMMENT '布防状态'")
    private String embileBfState;

    @Column(columnDefinition = "varchar(30) COMMENT '运营公司名称'")
    private String operatorName;

    @Column(columnDefinition = "INT COMMENT '运营公司Id'", nullable = false)
    private Integer operatorId;

    @Column(columnDefinition = "INT COMMENT '归属地Id'", nullable = false)
    private Integer regionId;

    @Column(columnDefinition = "varchar(30) COMMENT '归属地名称'")
    private String regionName;

    @Column(columnDefinition = "INT COMMENT '仓库Id'", nullable = false)
    private Integer storehouseId;

    @Column(columnDefinition = "varchar(30) COMMENT '仓库名称'")
    private String storehouseName;

    @Column(columnDefinition = "varchar(30) COMMENT '无线服务商名称'")
    private String iotTypeName;

    @Column(columnDefinition = "INT COMMENT '无线服务商Id'", nullable = false)
    private Integer iotTypeId;

    @Column(columnDefinition = "varchar(32) COMMENT '所有人Id'")
    private String ownerId;

    @Column(columnDefinition = "varchar(32) COMMENT '安装点名称'")
    private String installSiteName;

    @Column(columnDefinition = "INT COMMENT '安装点Id' ", nullable = false)
    private Integer installSiteId;

    @Column(columnDefinition = "varchar(64) COMMENT '从属派出所Id'")
    private String policeId;

    @Column(columnDefinition = "varchar(64) COMMENT '从属派出所名称'")
    private String policeName;

    @Column(columnDefinition = "varchar(64) COMMENT '设备厂商名称'")
    private String manufactorName;

    @Column(columnDefinition = "INT COMMENT '设备厂商Id'", nullable = false)
    private Integer manufactorId;

    @Column(columnDefinition = "varchar(32) COMMENT '设备名称'")
    private String name;

    @Column(columnDefinition = "INT COMMENT '频率'")
    private Integer frequency;
    /**
     * 查看告警时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date viewDate;

    private Integer groupId;

    @Transient
    private String vendor;

    @PrePersist //@PreUpdate
    public void prePersist() {
        if (StringUtils.isEmpty(getEmbileBkState())) {
            setEmbileBkState("1");
        }
        if (StringUtils.isEmpty(getEmbileBfState())) {
            setEmbileBfState("1");
        }
    }
}