package com.kavy.springsecuritydemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kavy.springsecuritydemo.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 创建用户角色
     * @param userId
     * @param roleId
     * @return
     */
    int insertRole(long userId, long roleId);

    /**
     * 根据角色id查找角色
     * @param roleId
     * @return
     */
    Role findRoleById(long roleId);

    /**
     * 根据用户id查找该用户角色
     * @param userId
     * @return
     */
    List<Role> findRoleByUserId(@Param("userId") Integer userId);
}
