package com.ltx.config;

import cn.hutool.core.util.StrUtil;
import com.ltx.constant.Constant;
import com.ltx.entity.Result;
import com.ltx.entity.SecurityUser;
import com.ltx.entity.User;
import com.ltx.enums.ErrorCode;
import com.ltx.enums.JwsVerificationResult;
import com.ltx.filter.JwtFilter;
import com.ltx.util.JwtUtil;
import com.ltx.util.RedisUtil;
import com.ltx.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 安全配置
 * EnableWebSecurity: 添加security过滤器
 * EnableGlobalMethodSecurity(prePostEnabled = true): 启用预授权和后授权注解
 *
 * @author tianxing
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final RedisUtil redisUtil;

    /**
     * 配置过滤器链
     * mvcMatchers("/a").hasAnyAuthority("x","y"): 拥有x或y权限的用户可以访问/a
     * mvcMatchers("/a").hasAuthority("x"): 拥有x权限的用户可以访问/a
     * anyRequest().authenticated(): 任何请求都需要登录
     * formLogin().permitAll(): 允许表单登录
     * 没有配置mvcMatchers: 登录成功就可以访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 在UsernamePasswordAuthenticationFilter前添加Jwt过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // 任何请求都需要认证
        http.authorizeRequests().anyRequest().authenticated();
        // 登录成功后生成一个JWS -> 设置登录成功处理器和登录失败处理器
        http.formLogin().successHandler((request, response, authentication) -> {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            User user = securityUser.getUser();
            // 用户授权信息
            List<String> authorities = securityUser.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            // 生成JWS
            String jws = JwtUtil.createJws(user, authorities, Constant.TWO_HOURS);
            // key = login:token:<jws>
            String key = Constant.LOGIN_TOKEN_KEY + jws;
            // JWS存入Redis -> 跟JWS过期时间保持一致 -> 解决退出登录时JWS不能过期的问题
            redisUtil.set(key, "", 2, TimeUnit.HOURS);
            Result result = Result.success("登录成功").put(Constant.TOKEN, jws);
            ServletUtil.write(response, result);
        }).failureHandler((request, response, exception) -> {
            Result result = Result.fail(ErrorCode.LOGIN_FAILED);
            ServletUtil.write(response, result);
        }).permitAll();
        // 设置退出成功处理器
        http.logout().logoutSuccessHandler((request, response, authentication) -> {
            String jws = request.getHeader(Constant.TOKEN);
            // 如果token为空
            if (StrUtil.isBlank(jws)) {
                Result result = Result.fail(ErrorCode.TOKEN_IS_NULL);
                ServletUtil.write(response, result);
                return;
            }
            // 校验token
            JwsVerificationResult verificationResult = JwtUtil.verifyJws(jws);
            // 如果token过期
            if (verificationResult == JwsVerificationResult.EXPIRED) {
                Result result = Result.fail(ErrorCode.TOKEN_EXPIRED);
                ServletUtil.write(response, result);
                return;
            }
            // 如果token无效
            if (verificationResult == JwsVerificationResult.INVALID) {
                Result result = Result.fail(ErrorCode.TOKEN_INVALID);
                ServletUtil.write(response, result);
                return;
            }
            // key = login:token:<jws>
            String key = Constant.LOGIN_TOKEN_KEY + jws;
            // 如果用户已退出
            if (Boolean.FALSE.equals(redisUtil.hasKey(key))) {
                Result result = Result.fail(ErrorCode.USER_HAS_EXITED);
                ServletUtil.write(response, result);
                return;
            }
            // 删除对应的key
            redisUtil.delete(key);
            ServletUtil.write(response, Result.success("退出成功"));
        });
        // 设置访问拒绝处理器
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            Result result = Result.fail(ErrorCode.UNAUTHORIZED);
            ServletUtil.write(response, result);
        });
        http.csrf().disable();
        // 不创建session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    /**
     * 获取BCrypt编码器 -> 对前端明文进行编码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
