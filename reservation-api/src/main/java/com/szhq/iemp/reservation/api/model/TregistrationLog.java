package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_registration_log")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TregistrationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registerLogId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long registerId;

    private Integer type;

    private String oldImei;

    private String newImei;

    private String userId;

    private String oldPlateNo;

    private String newPlateNo;

    private Integer operatorId;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "varchar(2000)")
    private String oldData;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    private String newData;

}