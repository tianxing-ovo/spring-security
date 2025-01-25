package com.ltx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误状态码枚举
 *
 * @author tianxing
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNAUTHORIZED(201, "权限不足"),
    LOGIN_FAILED(202, "登录失败"),
    TOKEN_IS_NULL(203, "token为空"),
    TOKEN_EXPIRED(204, "token过期"),
    TOKEN_INVALID(205, "token无效"),
    USER_HAS_EXITED(206, "用户已退出");

    private final int code;
    private final String message;
}
