package com.szhq.iemp.common.vo;

import com.szhq.iemp.common.constant.ResultConstant;

/**
 * 系统常量类
 */
public class Result extends BaseResult {

    public Result(ResultConstant nbiotResultConstant, Object data) {
        super(nbiotResultConstant.getCode(), nbiotResultConstant.getMessage(), data);
    }

    public Result(int code, String msg, Object data) {
        super(code, msg, data);
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "Result [code=" + code + ", message=" + message + ", data=" + data + "]";
    }


}
