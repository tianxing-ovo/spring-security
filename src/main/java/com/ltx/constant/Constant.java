package com.ltx.constant;

/**
 * @author tianxing
 */
public interface Constant {
    String TOKEN = "token";
    // 用户
    String USER = "user";
    // 权限列表
    String AUTHORITIES = "authorities";
    long TWO_HOURS = 1000 * 60 * 60 * 2;
    String LOGIN_TOKEN_KEY = "login:token:";
    // 角色前缀: Spring Security规定好的
    String ROLE_PREFIX = "ROLE_";
}
