package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 不良品库
 * @author wanghao
 */
@Entity(name = "t_device_defective_inventory")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceDefectiveInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String devtype;

    @Column(columnDefinition = "varchar(20) COMMENT '设备名称'")
    private String devname;

    @Column(columnDefinition = "varchar(30) COMMENT '设备号'", unique = true, nullable = false)
    private String imei;

    @Column(columnDefinition = "varchar(20)")
    private String imsi;

    @Column(columnDefinition = "varchar(30)")
    private String iccid;

    @Column(columnDefinition = "varchar(30)")
    private String snNo;

    @Column(columnDefinition = "varchar(32)")
    private Integer devstate;

    @Column(columnDefinition = "varchar(20) COMMENT '箱号'")
    private String boxNumber;

    private String devDesc;

    @Column(columnDefinition = "varchar(50) COMMENT '软件版本'", nullable = false)
    private String swVersion;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT default 1 COMMENT '仓库id'", nullable = false, name = "storehouse_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TdeviceStoreHouse storehouse;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT default 110000 COMMENT '归属地id'", nullable = false, name = "region_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TaddressRegion region;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '安装点id'", name = "install_site_id", referencedColumnName = "installSiteId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TinstallSite installSite;

    @Column(columnDefinition = "INT COMMENT '运营公司id'")
    private Integer operatorId;

    @Column(columnDefinition = "varchar(40) COMMENT '运营公司名称'")
    private String operatorName;
    /**
     * 无线服务商
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '无线服务商Id'", nullable = false, name = "iot_type_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TiotType iotType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '设备厂商id'", nullable = false, name = "manufactor_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TdeviceManufactor manufactor;

    @Column(columnDefinition = "varchar(100) COMMENT 'iotDeviceId'", nullable = false)
    private String iotDeviceId;
}
