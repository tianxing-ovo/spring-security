package org.spring.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {

    /**
     * 获取登录用户信息
     * Authentication extends Principal
     */
    @GetMapping("/getLoginUserInfo1")
    public Authentication getLoginUserInfo1(Authentication authentication) {
        return authentication;
    }

    /**
     * 获取登录用户信息
     */
    @GetMapping("/getLoginUserInfo2")
    public Principal getLoginUserInfo2(Principal principal) {
        return principal;
    }

    /**
     * 获取登录用户信息
     */
    @GetMapping("/getLoginUserInfo3")
    public Principal getLoginUserInfo3() {
        //通过安全上下文持有器获取登录用户信息
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
