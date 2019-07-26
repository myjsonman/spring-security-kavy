package com.kavy.springsecuritydemo.service.serviceImp;

import com.kavy.springsecuritydemo.entity.Role;
import com.kavy.springsecuritydemo.entity.User;
import com.kavy.springsecuritydemo.entity.UserDetail;
import com.kavy.springsecuritydemo.mapper.RoleMapper;
import com.kavy.springsecuritydemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
   private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            User user = userMapper.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(String.format("No userDetail found with username '%s'.", username));
            }
            //查询权限封装
        List<Role> roleByUserId = roleMapper.findRoleByUserId(user.getId());

        return new UserDetail(user.getUsername(),roleByUserId,user.getPassword());

}


}
