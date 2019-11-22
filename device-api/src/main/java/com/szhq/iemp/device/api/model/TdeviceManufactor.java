package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * 设备厂商
 */
@Entity(name = "t_device_manufactor")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceManufactor extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(32)")
    @NotEmpty(message = "设备厂商名称不能为空")
    private String name;
}
