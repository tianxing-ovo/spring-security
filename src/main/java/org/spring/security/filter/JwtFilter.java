package org.spring.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tianxingovo.common.R;
import io.github.tianxingovo.enums.ErrorCodeEnum;
import io.github.tianxingovo.jwt.JwtUtil;
import io.github.tianxingovo.redis.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.spring.security.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * jwt过滤器
 */
@Component
@AllArgsConstructor
public class JwtFilter implements Filter {


    private ObjectMapper om;

    private JwtUtil jwtUtil;

    private RedisUtil redisUtil;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI(); //请求URI
        //登录请求直接放行
        if ("/login".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            R r = R.error(ErrorCodeEnum.TOKEN_IS_NULL.getCode(), ErrorCodeEnum.TOKEN_IS_NULL.getMessage());
            write(r, response);
            return;
        }
        //校验token
        boolean b = jwtUtil.verifyToken(token);
        if (!b) {
            R r = R.error(ErrorCodeEnum.TOKEN_IS_MISTAKE.getCode(), ErrorCodeEnum.TOKEN_IS_MISTAKE.getMessage());
            write(r, response);
            return;
        }
        String key = "loginToken:" + token;
        String s = redisUtil.get(key);
        if (StringUtils.isBlank(s)) {
            R r = R.error(ErrorCodeEnum.USER_HAS_EXITED.getCode(), ErrorCodeEnum.USER_HAS_EXITED.getMessage());
            write(r, response);
            return;
        }
        //获取用户信息
        User user = om.readValue(jwtUtil.getUserInfo(token), User.class);
        //获取权限列表
        List<SimpleGrantedAuthority> authList = jwtUtil.getAuthList(token).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        //传递用户的认证信息,并将用户标识为已认证状态
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authList);
        //authenticationToken放到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
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
