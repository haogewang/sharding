package com.szhq.iemp.common.constant.enums.exception;

public enum RegionExceptionEnum {

    E_0003(3, "区域不存在"),
    E_0004(4, "区域值不能为空"),
    ;

    private int code;
    private String message;

    RegionExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
