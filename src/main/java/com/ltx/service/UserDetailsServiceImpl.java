package com.ltx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ltx.constant.Constant;
import com.ltx.entity.SecurityUser;
import com.ltx.entity.User;
import com.ltx.mapper.AuthorityMapper;
import com.ltx.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务
 *
 * @author tianxing
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthorityMapper authorityMapper;


    /**
     * 登录时调用此方法
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据username查询用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        Long id = user.getId();
        // 根据用户id查询用户所有角色和权限
        List<String> roleList = authorityMapper.getRolesById(id);
        List<String> authorityList = authorityMapper.getAuthById(id);
        // 存放用户的授权信息: 包括角色(Role)和权限(Permission)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 将角色加入授权信息
        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority(Constant.ROLE_PREFIX + role)));
        // 将权限加入授权信息
        authorityList.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
        return new SecurityUser(user, authorities);
    }
}
