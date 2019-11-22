package com.szhq.iemp.common.util;

import com.szhq.iemp.common.vo.Result;

public class ResultUtil {
	
    public static Result success(int code, String message, Object data){
        Result result = new Result(code, message ,data);
        return result;
    }

    public static Result error(int code, String message){
        Result result = new Result(code, message, null);
        return result;
    }
}
