package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 保单表
 */
@Entity(name = "t_policy_info")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpolicyInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(40) COMMENT '设备号'")
    private String imei;

    private String userId;

    //是否生效
    private Boolean isEffective = false;

    //保险名称
    private Integer nameCode;

    private String name;

    private Date activeTime;

    private Date startTime;

    private Date endTime;

    private String plateNo;

    private Integer operatorId;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "varchar(2000)")
    private String info;

    @Transient
    private List<Tinsurance> insuranceList;
}