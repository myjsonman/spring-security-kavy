package com.kavy.springsecuritydemo.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Role {

    /**
     * id
     */
    private Integer id;

    /**
     * rloe_name
     */
    private String roleName;

    /**
     * updatetime
     */
    private Timestamp updatetime;

    /**
     * role_desc
     */
    private String roleDesc;

}
