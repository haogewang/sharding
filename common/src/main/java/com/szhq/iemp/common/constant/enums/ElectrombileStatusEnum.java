package com.szhq.iemp.common.constant.enums;

public enum ElectrombileStatusEnum {

    NORMAL(true, "1"),
    UNNORMAL(false, "0");

    private boolean mode;
    private String message;


    ElectrombileStatusEnum(boolean code, String message) {
        this.mode = code;
        this.message = message;
    }

    public boolean getCode() {
        return mode;
    }

    public String getMessage() {
        return message;
    }

    public static String getValue(Boolean mode) {
        for (ElectrombileStatusEnum eenum : ElectrombileStatusEnum.values()) {
            if (eenum.getCode() == mode) {
                return eenum.getMessage();
            }
        }
        return null;
    }
}
