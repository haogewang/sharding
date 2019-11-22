package com.szhq.iemp.common.constant.enums.exception;

public enum ReservationExceptionEnum {

    E_0003(2, "该安装点今日预约名额已满"),
    E_0004(4, "保存预约失败");

    private int code;
    private String message;


    ReservationExceptionEnum(int code, String message) {
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
