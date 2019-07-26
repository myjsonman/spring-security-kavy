package com.kavy.springsecuritydemo.utils;

import com.kavy.springsecuritydemo.entity.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUserToken {
    private String token;
    private UserDetail userDetail;
}