package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity(name = "t_no_tracker_elec")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TnoTrackerElec extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "INT COMMENT '车辆品牌Id'")
    private Integer vendorId;

    @Column(columnDefinition = "INT COMMENT '车辆类型Id'")
    private Integer typeId;

    @Column(columnDefinition = "INT COMMENT '车辆颜色Id'")
    private Integer colorId;

    @Column(columnDefinition = "varchar(50) COMMENT '车架号'")
    private String vin;
    /**
     * 购买时间
     */
    private Date purchaseTime;

    @NotEmpty(message = "车牌号不能为空")
    @Column(columnDefinition = "varchar(32) COMMENT '车牌号'", nullable = false, unique = true)
    private String plateNumber;
    /**
     * 保险单号
     */
    @Column(columnDefinition = "varchar(32) COMMENT '保险单号'")
    private String policyNo;
    /**
     * 承保日期
     */
    private Date insuranceTime;
    /**
     * 电动车照片
     */
    @Column(columnDefinition = "varchar(300) COMMENT '电动车照片路径'")
    private String motorPhotoUrl;

    @Column(columnDefinition = "varchar(300) COMMENT '承诺书照片路径'")
    private String promisePhotoUrl;

    @Column(columnDefinition = "varchar(300) COMMENT '保险单照片路径'")
    private String policyPhotoUrl;
    /**
     * 电机号
     */
    @Column(columnDefinition = "varchar(32) COMMENT '电机号' ")
    private String motorNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(columnDefinition = "varchar(32) COMMENT '用户Id'", name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Tuser user;

}
