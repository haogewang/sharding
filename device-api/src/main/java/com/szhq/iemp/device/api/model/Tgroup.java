package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 分组
 */
@Entity(name = "t_group")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tgroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "组名称不能为空")
    private String name;

    @NotNull(message = "运营公司Id不能为空")
    @Column(columnDefinition = "INT COMMENT '运营公司Id'")
    private Integer operatorId;

    private Integer parentId;

    @NotNull(message = "组类型不能为空(1:设备分组 2:车辆分组)")
    private Integer type;

    @Formula("(select O.name from t_operator O where O.id = operator_id)")
    private String operatorName;

    @Formula("(select G.name from t_group G where G.id = parent_id)")
    private String parentName;

//    @Formula("(select count(DI.imei) from t_device_inventory DI where DI.group_id = id)")
    @Transient
    private Integer deviceCount;

//    @Formula("(select count(E.imei) from t_electrmobile E where E.group_id = id)")
    @Transient
    private Long elecCount;

    private Integer customType;

}