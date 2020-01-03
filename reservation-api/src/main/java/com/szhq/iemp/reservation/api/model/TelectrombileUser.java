package com.szhq.iemp.reservation.api.model;

import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_electrmobile_user")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class TelectrombileUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long electrombileId;

    private String userId;

//    private Long noTrackerElecId;

    private Integer operatorId;
    /**
     * 是否本人 0:不是 1：是
     */
    private Integer isOwner = 1;
}