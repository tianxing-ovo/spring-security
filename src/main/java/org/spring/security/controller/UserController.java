package org.spring.security.controller;

import common.R;
import org.spring.security.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * curl localhost:8080/root:发送http请求
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 查询
     * PreAuthorize:预授权,在方法执行前进行权限校验，决定是否允许方法的执行
     * PostAuthorize:后授权,在方法执行后对方法的结果进行权限校验，决定是否允许返回结果或其中的某个属性访问
     */
    @GetMapping
    @PreAuthorize("hasAuthority('query')")
    public R query() {
        return R.ok("query success");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('add')")
    public R add(@RequestBody User user) {
        return R.ok("add success").put("user", user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete')")
    public R delete(@PathVariable Long id) {
        return R.ok("delete success");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update')")
    public R update(@PathVariable Long id) {
        return R.ok("update success");
    }
}
