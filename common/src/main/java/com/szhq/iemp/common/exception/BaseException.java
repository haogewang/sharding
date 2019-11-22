package com.szhq.iemp.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {


    private static final long serialVersionUID = 4600931923831132904L;

    private int code;
    private Object[] params;

    public BaseException(int code, String message, Object... params) {
        super(message);
        this.code = code;
        this.params = params;
    }

}
