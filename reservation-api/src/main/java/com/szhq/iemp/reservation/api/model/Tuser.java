package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.resolver.DesensitizedAnnotation;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity(name = "user")
@Data
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tuser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "varchar(50)")
    private String id;

    //@NotEmpty(message = "系统Id不能为空")
    private String systemId;

    //@NotEmpty(message = "车主姓名不能为空")
    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String name;

    @JsonIgnore
    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(32)")
    private String nickname;

    // @NotEmpty(message = "证件号不能为空")
//  @Length(min = 6, max = 32, message = "证件号长度最长32位，最少6位")
//    @DesensitizedAnnotation(type = TypeEnum.ID_NUMBER)
    @Column(columnDefinition = "varchar(32)")
    private String idNumber;

//    @DesensitizedAnnotation(type = TypeEnum.HOME)
    @Column(columnDefinition = "varchar(100)")
    private String home;

    private String lastLoginTime;

    @Column(columnDefinition = "varchar(100)")
    private String company;


    @Column(columnDefinition = "varchar(150)")
    private String loginName;

//    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    @NotEmpty(message = "登录账号不能为空")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    //@NotNull(message = "身份类型不能为空")
    private Byte idType;

    private String birthplace;

    //@NotEmpty(message = "手机号不能为空")
//    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String contactPhone;

    @Column(columnDefinition = "varchar(20)")
    private String tenantId;

    private String extend1;

    private Integer status;

    private Integer iotTypeId;

    private Integer deviceStorehouseId;

    private Integer installSiteId;

    private Integer operatorId;

    private Integer residentId;

    @Column(columnDefinition = "varchar(400)")
    private String idNumberPhotoUrl;
    @Column(columnDefinition = "varchar(400)")
    private String idNumberPhotoBackUrl;

    @Column(columnDefinition = "varchar(400) COMMENT '承诺照片'")
    private String signPhotoUrl;

    private String createTime;

    private String updateTime;

}