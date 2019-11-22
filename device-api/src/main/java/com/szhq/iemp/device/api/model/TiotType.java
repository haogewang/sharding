package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * 无线服务商
 */
@Entity(name = "t_iot_type")
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TiotType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "无线服务商名称不能为空")
    private String name;

}