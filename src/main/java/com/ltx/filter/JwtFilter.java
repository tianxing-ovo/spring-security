package com.ltx.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.ltx.constant.Constant;
import com.ltx.entity.Result;
import com.ltx.entity.User;
import com.ltx.enums.ErrorCode;
import com.ltx.enums.JwsVerificationResult;
import com.ltx.util.JwtUtil;
import com.ltx.util.RedisUtil;
import com.ltx.util.ServletUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jwt过滤器
 *
 * @author tianxing
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final RedisUtil redisUtil;

    /**
     * 对每个请求执行一次过滤操作
     *
     * @param request  请求对象{@link HttpServletRequest}
     * @param response 响应对象{@link HttpServletResponse}
     * @param chain    过滤器链
     */
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        String uri = request.getRequestURI();
        // 登录请求不拦截
        if ("/login".equals(uri)) {
            chain.doFilter(request, response);
            return;
        }
        // 从请求头中获取jws
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
        Claims claims = JwtUtil.getPayLoad(jws);
        // 获取用户信息
        User user = Convert.convert(User.class, claims.get(Constant.USER));
        // 获取授权信息
        List<SimpleGrantedAuthority> authorities = Convert.toList(String.class, claims.get(Constant.AUTHORITIES))
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        // 传递用户的认证信息 -> 将用户标识为已认证状态
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        // authenticationToken放到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }
}
