package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 安装点
 * @author wanghao
 */
@Data
@Entity(name = "t_install_site")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TinstallSite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer installSiteId;

    @NotEmpty(message = "安装点名称不能为空")
    @Column(columnDefinition = "varchar(50) COMMENT '安装点名称'", nullable = false)
    private String name;

    @NotEmpty(message = "安装点地址不能为空")
    @Column(columnDefinition = "varchar(100) COMMENT '安装点地址'", nullable = false)
    private String address;

    @Column(columnDefinition = "varchar(12) COMMENT '纬度'")
    private String lat;
    @Column(columnDefinition = "varchar(12) COMMENT '经度'")
    private String lon;

    @Column(columnDefinition = "tinyint(1) DEFAULT '1' COMMENT '状态(1:可安装,0:不可安装)'")
    private Boolean status;

    @NotEmpty(message = "责任人不能为空")
    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String personInCharge;

    @NotEmpty(message = "电话不能为空")
    @Column(columnDefinition = "varchar(32) COMMENT '电话'", nullable = false)
    private String phone;

    @Column(columnDefinition = "varchar(32) COMMENT '营业时间'")
    private String businessHours;

    private String businessLicence;

    @NotNull(message = "省Id不能为空")
    @Column(columnDefinition = "INT COMMENT '省Id'")
    private Integer provinceId;

    @NotNull(message = "市Id不能为空")
    @Column(columnDefinition = "INT COMMENT '市Id'")
    private Integer cityId;

    @NotNull(message = "区域Id不能为空")
    @Column(columnDefinition = "INT COMMENT '从属区域Id'")
    private Integer regionId;

    @Column(columnDefinition = "varchar(32) COMMENT '从属区域名称'")
    private String regionName;

    @Column(columnDefinition = "INT COMMENT '运营公司Id'", nullable = false)
    private Integer operatorId;

    @Column(columnDefinition = "varchar(32) COMMENT '运营公司域名称'")
    private String operatorName;

    @Column(columnDefinition = "varchar(64) COMMENT '从属派出所Id'")
    private String policeId;

    private String policeName;

    @Column(columnDefinition = "INT COMMENT '每天最大预约安装数'")
    private Integer maxReservationCount;

    @Column(columnDefinition = "INT COMMENT '最大预约天数'")
    private Integer maxReservationDay;

    /**
     * 安装点设备总数
     */
    @Transient
    private Integer siteTotal;
    /**
     * 历史安装数
     */
    @Transient
    private Integer siteEquip;
    /**
     * 历史在线数
     */
    @Transient
    private Integer siteOnlineEquip;
    /**
     * 今日已安装数
     */
    @Transient
    private Integer todayInstalledCount;
    /**
     * 今日在线数
     */
    @Transient
    private Integer todayOnlineCount;
    /**
     * 安装点分数
     */
    @Transient
    private Double score;

    @PrePersist
    public void prePersist() {
        if (getStatus() == null) {
            setStatus(true);
        }
    }
}