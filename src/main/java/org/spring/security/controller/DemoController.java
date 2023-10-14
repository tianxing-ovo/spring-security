package org.spring.security.controller;


import io.github.tianxingovo.common.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    /**
     * 基于角色控制权限
     */
    @GetMapping("/demo")
    @PreAuthorize("hasAnyRole('admin','manager')")
    public R demo() {
        return R.ok();
    }
}
