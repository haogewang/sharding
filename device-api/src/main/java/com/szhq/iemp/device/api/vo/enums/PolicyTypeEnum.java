package com.szhq.iemp.device.api.vo.enums;

import java.util.Objects;

public enum PolicyTypeEnum {

    DSZ("第三者责任险", 1),
    RSYW("人身意外险", 2),
    ZCDQ("整车盗抢险", 3);

    private String type;
    private Integer code;

    PolicyTypeEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static String getTypeByCode(Integer code) {
        for (PolicyTypeEnum policyEnum : PolicyTypeEnum.values()) {
            if (Objects.equals(policyEnum.getCode(), code)) {
                return policyEnum.getType();
            }
        }
        return null;
    }
}
