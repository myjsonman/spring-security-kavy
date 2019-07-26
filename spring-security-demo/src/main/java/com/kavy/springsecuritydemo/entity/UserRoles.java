package com.kavy.springsecuritydemo.entity;

import lombok.Data;

@Data
public class UserRoles {
    private  Integer id;
    private  Integer userId;
    private  Integer roleId;
    private String roleStr;

}
