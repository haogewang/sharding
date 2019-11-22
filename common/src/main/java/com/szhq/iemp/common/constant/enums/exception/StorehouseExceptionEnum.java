package com.szhq.iemp.common.constant.enums.exception;

public enum StorehouseExceptionEnum {

    E_0003(3, "上级仓库不存在"),
    E_0004(4, "该仓库下有已入库设备,不能删除"),
    E_0005(5, "该仓库子仓库下有已入库设备,不能删除"),
    E_0006(6, "仓库Id不能为空"),
    E_0007(7, "保险不存在"),
    E_0008(8, "创建运营公司仓库失败"),
    E_0009(9, "上级仓库不能是自己"),
    E_00010(10, "仓库名称不能为空"),
    E_00011(11, "仓库名称已存在"),
    E_00012(12, "仓库不存在"),
    ;

    private int code;
    private String message;


    StorehouseExceptionEnum(int code, String message) {
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
