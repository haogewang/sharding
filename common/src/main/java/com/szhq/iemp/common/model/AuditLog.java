package com.szhq.iemp.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author wanghao
 * @date 2020/1/2
 */
@Entity(name = "t_audit_log")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuditLog extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(2000) COMMENT '新数据'")
    private String newData;

    @Column(columnDefinition = "varchar(2000) COMMENT '旧数据'")
    private String oldData;

    private String operation;

    private String ip;

    private Integer operatorId;
}
