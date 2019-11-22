package com.szhq.iemp.device.api.vo.enums;

import java.util.Objects;

public enum PolicyNameEnum {

    ZYWL("中移物联", 1),
    PA("中国平安", 2);

    private String policyName;
    private Integer code;

    private PolicyNameEnum(String name, Integer code) {
        this.policyName = name;
        this.code = code;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static String getNameByCode(Integer code) {
        for (PolicyNameEnum policyEnum : PolicyNameEnum.values()) {
            if (Objects.equals(policyEnum.getCode(), code)) {
                return policyEnum.getPolicyName();
            }
        }
        return null;
    }
}
