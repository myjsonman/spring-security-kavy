package com.kavy.springsecuritydemo.service;

import com.kavy.springsecuritydemo.entity.User;
import com.kavy.springsecuritydemo.utils.ResponseUserToken;


public interface UserService {

    /**
     * 注册用户
     * @return
     */
    void register(User user,String str);


    /**
     * 登陆
     * @param username
     * @param password
     * @return
     */
    ResponseUserToken login(String username, String password);





}
