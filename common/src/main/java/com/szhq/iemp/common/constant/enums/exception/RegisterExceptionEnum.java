package com.szhq.iemp.common.constant.enums.exception;

public enum RegisterExceptionEnum {

//    E_0002(2, "设备未注册"),
    E_0005(5, "电动车绑定失败"),
    E_0006(6, "新增备案信息失败"),
	E_0007(7, "未入库、已入库未分配的设备不可以注册"),
    E_0008(8, "未入库不允许绑定"),
//	E_00010(10, "该设备已安装"),
//	E_00012(12, "设备号不能为空"),
//	E_00013(13, "该备案信息不存在"),
//	E_00014(14, "电机号已存在"),
//	E_00015(15, "该备案信息有误"),
	E_00016(16, "该账号所属运营公司与设备运营公司不符");
//    E_00017(17, "该设备已绑定"),
//    E_00018(18, "登录账号已存在"),
//    E_00019(19, "登录账号不能为空"),
//    E_00020(20, "修改备案信息失败"),
//    E_00021(21, "删除备案信息失败");

    private int code;
    private String message;


    RegisterExceptionEnum(int code, String message) {
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
