package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 激活设备表
 */
@Entity(name = "t_activator")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TactiveInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(40) COMMENT '设备号'", nullable = false)
    private String imei;

    @Column(columnDefinition = "varchar(50) COMMENT '激活人员Id'", nullable = false)
    private String activatorId;

    private String activatorName;

    private Integer operatorId;

    private Integer storehouseId;
    //1:激活  0：退货
    private Integer mode;

    private String groupId;

}