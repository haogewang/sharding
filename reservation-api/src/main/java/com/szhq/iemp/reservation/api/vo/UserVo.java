package com.szhq.iemp.reservation.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.resolver.DesensitizedAnnotation;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private String id;

    private String systemId;

    private String name;

    private String password;

    private String nickname;

    @DesensitizedAnnotation(type = TypeEnum.ID_NUMBER)
    private String idNumber;

    @DesensitizedAnnotation(type = TypeEnum.HOME)
    private String home;

    private String lastLoginTime;

    private String company;

    private String loginName;

    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String phone;

    private String email;

    private Byte idType;

    private String birthplace;

    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String contactPhone;

    private String tenantId;

    private String extend1;

    private Integer status;

    private Integer iotTypeId;

    private Integer deviceStorehouseId;

    private Integer installSiteId;

    private Integer operatorId;

    private Integer residentId;

    private String idNumberPhotoUrl;

    private String idNumberPhotoBackUrl;

    private String signPhotoUrl;

    private String createTime;

    private String updateTime;

}