package com.szhq.iemp.common.constant.enums.exception;

public enum UserExceptionEnum {

    E_0004(4, "用户不存在"),
    E_0009(9, "该用户不属于任何用户组")
    ;

    private int code;
    private String message;


    UserExceptionEnum(int code, String message) {
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
