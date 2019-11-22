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
 * 设备仓库
 */
@Entity(name = "t_device_storehouse")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceStoreHouse extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(32) COMMENT '仓库名称'")
    private String name;

    @Column(columnDefinition = "varchar(200) COMMENT '仓库地址'")
    private String address;

    @Column(columnDefinition = "varchar(32) COMMENT '经度'")
    private String lon;

    @Column(columnDefinition = "varchar(32) COMMENT '纬度'")
    private String lat;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT COMMENT '父Id'", name = "parent_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TdeviceStoreHouse parent;

    @Column(columnDefinition = "INT COMMENT '级别'", nullable = false)
    private Integer storeLevel = 1;

    private Integer operatorId;

    //是否激活
    private Boolean isActive = true;

    private Integer policyNameCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "INT default 110000 COMMENT '归属地id'", nullable = false, name = "region_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TaddressRegion region;

    @Transient
    private String operatorName;

    @PrePersist
    public void prePersist() {
        if (getParent() == null) {
            TdeviceStoreHouse deviceStoreHouse = new TdeviceStoreHouse();
            deviceStoreHouse.setId(0);
            setParent(deviceStoreHouse);
        }
    }

}
