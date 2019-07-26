package com.kavy.springsecuritydemo.controller;

import com.kavy.springsecuritydemo.entity.User;
import com.kavy.springsecuritydemo.result.ResultCode;
import com.kavy.springsecuritydemo.result.ResultJson;
import com.kavy.springsecuritydemo.service.UserService;
import com.kavy.springsecuritydemo.utils.ResponseUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    UserService userService;

    @GetMapping("/hello")
    public String hello(){

        return "hi security!";
    }

    /**
     * 注册
     * @param user
     */
    @PostMapping("/register")
    public ResultJson signUp( User user ,String str) {
        if (user==null){
            ResultJson.failure(ResultCode.BAD_REQUEST);
        }
        userService.register(user,str);

         return ResultJson.success();

    }

    /**
     * 获取token
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ResultJson<ResponseUserToken> login(@RequestBody User user) {

        final ResponseUserToken response = userService.login(user.getUsername(), user.getPassword());
        return ResultJson.ok(response);
    }



}
