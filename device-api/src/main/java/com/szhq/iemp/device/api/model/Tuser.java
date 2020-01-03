package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "user")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tuser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "varchar(50)")
    private String id;

    @NotEmpty(message = "系统Id不能为空")
    private String systemId;

    @NotEmpty(message = "车主姓名不能为空")
    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String name;

    @JsonIgnore
    @Column(columnDefinition = "varchar(32)", nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(32)")
    private String nickname;

    @NotEmpty(message = "证件号不能为空")
    @Length(min = 6, max = 32, message = "证件号长度最长32位，最少6位")
    @Column(columnDefinition = "varchar(32)")
    private String idNumber;

    @Column(columnDefinition = "varchar(100)")
    private String home;

    private String lastLoginTime;

    @Column(columnDefinition = "varchar(100)")
    private String company;


    @Column(columnDefinition = "varchar(150)")
    private String loginName;

    @NotEmpty(message = "登录账号不能为空")
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "身份类型不能为空")
    private Byte idType;

    private String birthplace;
    @NotEmpty(message = "手机号不能为空")
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

    private String createTime;

    private String updateTime;

    @Transient
    private String sex;

    //签名url
    @Transient
    private String signUrl;

}