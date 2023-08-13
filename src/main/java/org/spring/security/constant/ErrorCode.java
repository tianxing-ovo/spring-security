package org.spring.security.constant;

/**
 * 错误状态码枚举
 */
public enum ErrorCode {
    UNAUTHORIZED(201),
    LOGIN_FAILED(202),
    TOKEN_IS_NULL(203),
    TOKEN_IS_MISTAKE(204),
    USER_HAS_EXITED(205);


    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
