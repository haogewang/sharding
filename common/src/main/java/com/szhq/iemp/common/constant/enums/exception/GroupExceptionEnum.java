package com.szhq.iemp.common.constant.enums.exception;

public enum GroupExceptionEnum {

    E_0003(3, "该组下有已分配设备，不能删除"),
    E_0004(4, "该组不存在"),
    E_0005(5, "新增组失败"),
    E_0006(6, "删除组失败"),
    E_0007(7, "该账号所属运营公司与组所属运营公司不符"),
    E_0008(8, "组Id不能为空"),
    E_0009(9, "有设备已分组，不能再次分组"),
    E_00010(10, "组类型不正确，请确认"),
    E_00011(11, "上级分组不存在"),
    E_00012(12, "上级分组不能是自己"),
    E_00013(13, "组名称已存在"),
    ;

    private int code;
    private String message;


    GroupExceptionEnum(int code, String message) {
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
