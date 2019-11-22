package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;

/**
 * 分配记录
 */
@Entity(name = "t_device_dispatch_history")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceDispatchHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;

    @Column(nullable = false)
    private String imei;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "varchar(2000)")
    private String deviceOld;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "varchar(2000)")
    private String deviceNew;

    private String boxNumber;

    private Integer oldsiteId;

    private Integer newsiteId;

//    @Formula("(select i.name from t_install_site i where i.install_site_id = install_site_id_old)")
    private String oldsiteName;

//    @Formula("(select i.name from t_install_site i where i.install_site_id = install_site_id_new)")
    private String newsiteName;

    private String isp;

    private String snNo;

    @Column(columnDefinition = "INT COMMENT '运营公司id'")
    private Integer operatorId;

    @Column(columnDefinition = "varchar(20) COMMENT '运营公司名称'")
    private String operatorName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT default 1 COMMENT '仓库id'", name = "storehouse_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TdeviceStoreHouse storeHouse;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT default 110000 COMMENT '归属地id'", name = "region_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TaddressRegion region;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '无线服务商Id'", name = "iot_type_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TiotType iotType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '设备厂商id'", name = "manufactor_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TdeviceManufactor manufactor;

}
