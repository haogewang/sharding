package com.szhq.iemp.common.vo;

import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.resolver.DesensitizedAnnotation;

import java.io.Serializable;

/**
 * @author wanghao
 * @date 2019/11/8
 */
public class BaseInfo implements Serializable {

    private static final long serialVersionUID = 7786232937067394005L;

    @DesensitizedAnnotation(type = TypeEnum.PHONE)
    private String phone;

    @DesensitizedAnnotation(type = TypeEnum.ID_NUMBER)
    private String idNumber;

    @DesensitizedAnnotation(type = TypeEnum.HOME)
    private String home;
}
