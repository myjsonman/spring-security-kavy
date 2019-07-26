package com.kavy.springsecuritydemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kavy.springsecuritydemo.entity.User;
import com.kavy.springsecuritydemo.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findByUsername(String username);


}
