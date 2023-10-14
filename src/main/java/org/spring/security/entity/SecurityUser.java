package org.spring.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 用户信息
 */
@Data
public class SecurityUser implements UserDetails {

    private static final long serialVersionUID = -2672594260930233276L;

    private final User user;
    private final List<SimpleGrantedAuthority> list; // 权限列表

    public SecurityUser(User user, List<SimpleGrantedAuthority> list) {
        this.user = user;
        this.list = list;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return list;
    }

    @Override
    public String getPassword() {
        String password = user.getPassword();
        user.setPassword(""); // 擦除密码,防止传到前端
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired() == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired() == 1;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() == 1;
    }
}
