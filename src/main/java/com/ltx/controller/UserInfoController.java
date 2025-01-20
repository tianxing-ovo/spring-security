package com.ltx.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 用户信息控制器
 *
 * @author tianxing
 */
@RestController
@RequestMapping("/me")
public class UserInfoController {

    /**
     * 获取登录用户信息
     *
     * @param authentication 用户信息
     * @return 用户信息
     */
    @GetMapping("/authentication")
    public Authentication getLoginUserInfoFromAuthentication(Authentication authentication) {
        return authentication;
    }

    /**
     * 获取登录用户信息
     *
     * @param principal 用户信息
     * @return 用户信息
     */
    @GetMapping("/principal")
    public Principal getLoginUserInfoFromPrincipal(Principal principal) {
        return principal;
    }

    /**
     * 获取登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/security-context")
    public Principal getLoginUserInfoFromSecurityContextHolder() {
        // 通过安全上下文持有器获取登录用户信息
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
