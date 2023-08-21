package org.spring.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.spring.security.dao.AuthorityDao;
import org.spring.security.dao.UserDao;
import org.spring.security.entity.SecurityUser;
import org.spring.security.entity.User;
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
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    UserDao userDao;

    @Resource
    AuthorityDao authorityDao;


    /**
     * 登录时调用此方法
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据username查询用户信息
        User user = userDao.selectOne(new QueryWrapper<User>().eq("username", username));
        Long id = user.getId();
        // 根据用户id查询用户所有角色和权限
        List<String> list = authorityDao.getAuthById(id);
        List<String> roleList = authorityDao.getRolesById(id);
        // 将角色和权限加入权限列表中，前缀"ROLE_"是Spring Security规定的
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        roleList.forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role)));
        list.forEach(a -> authorityList.add(new SimpleGrantedAuthority(a)));
        return new SecurityUser(user, authorityList);
    }
}
