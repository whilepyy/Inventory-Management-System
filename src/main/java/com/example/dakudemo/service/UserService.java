package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface UserService {

    /**通过id查询用户信息**/
    public String getUserNameById(Integer id);
    /**通过id查询用户displayName**/
    public String getDisplayNameById(Integer id);
/**通过用户名查询用户**/
    public User getUserByUserName(String username);
    /**通过用户名查询角色**/
    public User getRolesByUserName(String user_name);

    /**根据role_id查询权限集合**/
    public List<Permission> getPermsByRoleId(Integer roleId);

    /**查询所有角色和对应权限**/
    public List<UrlFilter> getAllUrlFilters();

    /**注册添加用户**/
    public boolean addUser(User user);

    /**为指定用户名的用户添加角色**/
    public boolean addRoleForUserByUserName(String userName, String roleName);

    /**通过department名称查询对应department_id**/
    public Integer getDepartmentIdByDepartmentName(String department_name);

    /**通过department_id查询对应department_name**/
    public String getDepartmentNameByDepartmentId(Integer id);

    /**添加一个部门**/
    public boolean addDepartment(String department_name);

    public Integer getDepartmentIdByUserId(Integer user_id);

    /** 根据用户id查询用户系统 */
    public List<Integer> getSystemIdByUserId(Integer user_id);

    /** 根据显示名称查询用户id */
    public Integer getUserIdByDisplayName(String displayName);
}
