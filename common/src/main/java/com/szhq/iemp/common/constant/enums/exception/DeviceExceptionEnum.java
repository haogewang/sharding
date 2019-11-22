package com.szhq.iemp.common.constant.enums.exception;

public enum DeviceExceptionEnum {

    E_0000(0, "设备不存在"),
    E_0001(1, "设备厂商不存在"),
    E_0002(2, "归属地不存在"),

    E_0004(4, "无线服务商不存在"),
    E_00013(13, "箱号不存在"),

    E_0007(7, "设备已入库"),
    E_0008(8, "更换的设备安装点不一致"),
    E_0009(9, "设备未入库"),
    E_00010(10, "设备已分配"),
    E_00032(32, "该设备已安装"),

    E_00012(12, "设备未入库，不能退库"),
    E_00018(18, "该运营公司下仓库有入库设备，不能删除"),


    E_00028(28, "该设备不属于任何分组"),
    E_00017(17, "该设备不属于任何运营公司"),
    E_00030(30, "该仓库与运营公司所属仓库不一致，请确认"),
    E_00031(31, "该箱子设备有不同运营商设备，请确认"),

    E_00033(33, "设备未激活，不能绑定"),
    E_00034(34, "设备已绑定，不能退货"),
    E_00038(38, "保险已经激活，不能退货"),
    E_00035(35, "设备已激活"),
    E_00036(36, "设备未激活"),
    E_00037(37, "设备绑定失败")

    ;

    private int code;
    private String message;


    DeviceExceptionEnum(int code, String message) {
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
