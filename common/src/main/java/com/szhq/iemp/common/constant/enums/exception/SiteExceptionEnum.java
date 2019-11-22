package com.szhq.iemp.common.constant.enums.exception;

public enum SiteExceptionEnum {

    E_0003(3, "该安装点下有分配设备，不能删除"),
    E_0004(4, "该安装点名称已存在"),
    E_0005(5, "该安装点不存在"),
    ;

    private int code;
    private String message;


    SiteExceptionEnum(int code, String message) {
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
