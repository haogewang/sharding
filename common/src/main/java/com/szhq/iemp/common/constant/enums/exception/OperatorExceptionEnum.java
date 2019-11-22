package com.szhq.iemp.common.constant.enums.exception;

public enum OperatorExceptionEnum {

    E_0002(2, "运营公司不存在"),
    E_0003(3, "上级运营公司不存在"),
    E_0004(4, "运营公司名称不能为空"),
    E_0005(5, "运营公司名称已存在"),
    E_0006(6, "上级运营公司不能是自己"),
    E_0007(7, "创建运营公司失败"),
    E_0008(8, "修改运营公司失败"),
    E_0009(9, "删除运营公司失败"),
    E_00010(10, "该运营公司下有设备，不能删除"),
    E_00011(11, "该账号所属运营公司与设备所属运营公司不符"),
    E_00012(12, "安装人员运营公司不能为空"),
    E_00013(13, "该账号所属运营公司与箱子所属运营公司不符"),
    E_00014(14, "该账号所属运营公司与分配安装点所属运营公司不符"),
    E_00015(15, "该账号不属于任何运营公司，无权限操作"),
    ;

    private int code;
    private String message;


    OperatorExceptionEnum(int code, String message) {
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
