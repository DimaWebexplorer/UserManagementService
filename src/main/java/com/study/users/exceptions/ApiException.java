package com.study.users.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private final Object data;

    public ApiException(ErrorCode code) {
        this(code, null);
    }

    public ApiException(ErrorCode code, Object data) {
        super(code.name());
        this.code = code;
        this.data = data;
    }
}
