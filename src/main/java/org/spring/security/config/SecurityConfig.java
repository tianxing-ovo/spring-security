package org.spring.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.R;
import enums.ErrorCode;
import jwt.JwtUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.spring.security.entity.SecurityUser;
import org.spring.security.filter.JwtFilter;
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
import redis.RedisUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 安全配置
 * EnableWebSecurity:添加security过滤器
 * EnableGlobalMethodSecurity(prePostEnabled = true):启用预授权和后授权注解
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Resource
    ObjectMapper om;
    @Resource
    JwtFilter jwtFilter;
    @Resource
    RedisUtil redisUtil;
    @Resource
    JwtUtil jwtUtil;

    /**
     * 配置过滤器链
     * mvcMatchers("/a").hasAnyAuthority("x","y"):拥有x或y权限的用户可以访问/a
     * mvcMatchers("/a").hasAuthority("x"):拥有x权限的用户可以访问/a
     * anyRequest().authenticated():任何请求都需要登录
     * formLogin().permitAll():允许表单登录
     * 如果没有配置mvcMatchers,登录成功就可以访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //在UsernamePasswordAuthenticationFilter前添加jwt过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests().anyRequest().authenticated();//任何请求都需要认证
        //登录成功后生成一个jwtToken,设置登录成功处理器和登录失败处理器
        http.formLogin().successHandler((request, response, authentication) -> {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            String userInfo = om.writeValueAsString(securityUser.getUser());//用户信息json字符串
            List<SimpleGrantedAuthority> list = securityUser.getList();//用户权限
            List<String> authList = list.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
            String token = jwtUtil.createToken(userInfo, authList);
            String key = "loginToken:" + token;
            //authentication存入redis,解决退出时jwt不能过期的问题,跟jwt过期时间保持一致
            redisUtil.set(key, om.writeValueAsString(authentication), 2, TimeUnit.HOURS);
            R r = R.ok("登录成功").put("token", token);
            write(r, response);
        }).failureHandler((request, response, exception) -> {
            R r = R.error(ErrorCode.LOGIN_FAILED.getCode(), ErrorCode.LOGIN_FAILED.getMessage());
            write(r, response);
        }).permitAll();//允许访问登录页面
        //设置退出成功处理器
        http.logout().logoutSuccessHandler((request, response, authentication) -> {
            String token = request.getHeader("token");
            if (StringUtils.isBlank(token)) {
                R r = R.error(ErrorCode.TOKEN_IS_NULL.getCode(), ErrorCode.TOKEN_IS_NULL.getMessage());
                write(r, response);
                return;
            }
            boolean b = jwtUtil.verifyToken(token);
            if (!b) {
                R r = R.error(ErrorCode.TOKEN_IS_MISTAKE.getCode(), ErrorCode.TOKEN_IS_MISTAKE.getMessage());
                write(r, response);
                return;
            }
            String key = "loginToken:" + token;
            //删除authentication
            redisUtil.delete(key);
            R r = R.ok("退出成功");
            write(r, response);
        });
        //设置访问拒绝处理器
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            R r = R.error(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
            write(r, response);
        });
        http.csrf().disable();
        //不创建session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    /**
     * 获取BCrypt编码器,对前端明文进行编码
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 返回浏览器的json字符串
     */
    @SneakyThrows
    public void write(R r, HttpServletResponse response) {
        String s = om.writeValueAsString(r);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(s);
        writer.flush();
    }
}
