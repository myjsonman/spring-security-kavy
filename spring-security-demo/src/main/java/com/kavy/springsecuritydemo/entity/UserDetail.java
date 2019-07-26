package com.kavy.springsecuritydemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
@Data
public class UserDetail implements UserDetails {

    private long id;
    private String username;
    private String password;
    private List<Role> roles;
    //private Collection<? extends GrantedAuthority> authorities;

    public UserDetail(
            String username,
            List<Role> roles,
            String password) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
    //getAuthorities获取用户包含的权限，返回权限集合，权限是一个继承了GrantedAuthority的对象；
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据自定义逻辑来返回用户权限，如果用户权限返回空或者和拦截路径对应权限不同，验证不通过
        if (!roles.isEmpty()){

            List<GrantedAuthority> authorities = new ArrayList<>();
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            }
            return authorities;
        }
        return null;
    }

    //getPassword和getUsername用于获取密码和用户名；
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //isAccountNonExpired方法返回boolean类型，用于判断账户是否未过期，未过期返回true反之返回false；
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //isAccountNonLocked方法用于判断账户是否未锁定；
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //isCredentialsNonExpired用于判断用户凭证是否没过期，即密码是否未过期；
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //isEnabled方法用于判断用户是否可用。
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
