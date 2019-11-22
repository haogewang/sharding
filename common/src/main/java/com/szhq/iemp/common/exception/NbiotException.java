package com.szhq.iemp.common.exception;

public class NbiotException extends BaseException {

    private static final long serialVersionUID = 1L;

    public NbiotException(int code, String defaultMessage, Object... params) {
        super(code, defaultMessage, params);
    }

}
