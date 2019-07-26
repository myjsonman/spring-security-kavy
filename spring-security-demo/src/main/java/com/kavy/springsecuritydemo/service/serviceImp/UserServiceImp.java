package com.kavy.springsecuritydemo.service.serviceImp;

import com.kavy.springsecuritydemo.entity.User;
import com.kavy.springsecuritydemo.entity.UserDetail;
import com.kavy.springsecuritydemo.entity.UserRoles;
import com.kavy.springsecuritydemo.exception.CustomException;
import com.kavy.springsecuritydemo.mapper.UserMapper;
import com.kavy.springsecuritydemo.mapper.UserRolesMapper;
import com.kavy.springsecuritydemo.result.ResultCode;
import com.kavy.springsecuritydemo.result.ResultJson;
import com.kavy.springsecuritydemo.service.UserService;
import com.kavy.springsecuritydemo.utils.JwtUtils;
import com.kavy.springsecuritydemo.utils.ResponseUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;


import java.util.Date;
@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private UserRolesMapper userRolesMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public void register(User user,String str) {

        //查询用户
        User oldUser = userMapper.findByUsername(user.getUsername());

        if (oldUser != null) {

           throw new CustomException(ResultJson.failure(ResultCode.BAD_REQUEST, "用户已存在"));
        }
        //加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUpdatetime(new Date(System.currentTimeMillis()));
        user.setStatus("0");
        userMapper.insert(user);

        if (StringUtils.isNotBlank(str)){
            //权限插入
            String[] roles = str.split(",");
            for (String role : roles) {
                //如果原先有绑定权限就删除
               // userRolesMapper.deleteById(user.getId());

                UserRoles userRoles = new UserRoles();
                userRoles.setUserId(user.getId());
                userRoles.setRoleId(Integer.parseInt(role));
                userRoles.setRoleStr(str);
                userRolesMapper.insert(userRoles);
            }
        }

    }

    @Override
    public ResponseUserToken login(String username, String password) {
        //用户验证
        final Authentication authentication = authenticate(username, password);
        //存储认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //生成token ，查看源代码会发现调用getPrincipal()方法会返回一个实现了`UserDetails`接口的对象
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        //通过工具类生成token
         final String token = "Bearer "+jwtUtils.generateAccessToken(userDetail);

        //存储token
        jwtUtils.putToken(username, token);
        // 学习 测试用,把用户的信息也返回了
        return new ResponseUserToken(token, userDetail);
    }


    private Authentication authenticate(String username, String password) {
        try {
            //该方法会去调用userDetailsService.loadUserByUsername()去验证用户名和密码，如果正确，则存储该用户名密码到“security 的 context中”
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new CustomException(ResultJson.failure(ResultCode.LOGIN_ERROR, e.getMessage()));
        }
    }
}
