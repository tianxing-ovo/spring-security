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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    UserDao userDao;

    @Resource
    AuthorityDao authorityDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据username查询用户信息
        User user = userDao.selectOne(new QueryWrapper<User>().eq("username", username));
        //根据用户id查询用户所有权限
        List<String> list = authorityDao.getAuthById(user.getId());
        List<SimpleGrantedAuthority> authorityList = list.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new SecurityUser(user, authorityList);
    }
}
