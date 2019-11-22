package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 保险表
 */
@Entity(name = "t_insurance")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tinsurance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //保险名称编码
    @NotNull(message = "保险名称不能为空")
    private Integer nameCode;

    private String name;
    //险种
    @NotNull(message = "保险种类不能为空")
    private Integer typeCode;

    private String type;
    //保额
    private String coverage;
    //车龄
    private String carAge;
    //赔偿标准
    private String compensation;
    //保险项目
    private String insuranceProject;
    //保险期限
    private Integer period;

}