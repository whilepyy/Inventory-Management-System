package com.example.dakudemo.service;

import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Permission;
import com.example.dakudemo.entity.UrlFilter;
import com.example.dakudemo.entity.User;
import com.example.dakudemo.mapper.OutApprovalMapper;
import com.example.dakudemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:10
 */
@Service
public class UserImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**通过id查询用户信息**/
    public String getUserNameById(Integer id){
        return userMapper.getUserNameById(id);
    }
    /**通过id查询用户信息**/
    public String getDisplayNameById(Integer id){
        return userMapper.getDisplayNameById(id);
    }
	/**通过用户名查询用户**/
    public User getUserByUserName(String username){
        return userMapper.getUserByUserName(username);
    }
   

    /**通过用户名查询角色**/
    public User getRolesByUserName(String user_name){
        return userMapper.getRolesByUserName(user_name);
    }

    /**根据role_id查询权限集合**/
    public List<Permission> getPermsByRoleId(Integer roleId){
        return userMapper.getPermsByRoleId(roleId);
    }

    /**查询所有角色和对应权限**/
    public List<UrlFilter> getAllUrlFilters(){
        return userMapper.getAllUrlFilters();
    }

    /**注册添加用户**/
    public boolean addUser(User user){
        return userMapper.addUser(user);
    }
    /**为指定用户名的用户添加角色**/
    public boolean addRoleForUserByUserName(String userName, String roleName){
        Integer role_id = userMapper.getRoleIdByRoleName(roleName);
        Integer user_id = userMapper.getUserIdByUserName(userName);
        return userMapper.addRoleForUserById(user_id, role_id);
    }
    /**通过department名称查询对应department_id**/
    public Integer getDepartmentIdByDepartmentName(String department_name){
        return userMapper.getDepartmentIdByDepartmentName(department_name);
    }

    /**通过department_id查询对应department_name**/
    public String getDepartmentNameByDepartmentId(Integer id){
        return userMapper.getDepartmentNameByDepartmentId(id);
    }

    /**添加一个部门**/
    public boolean addDepartment(String department_name){
        if(ObjectUtils.isEmpty(userMapper.getDepartmentIdByDepartmentName(department_name))){
            return userMapper.addDepartment(department_name);
        }
        return false;
    }

    public Integer getDepartmentIdByUserId(Integer user_id){
        return userMapper.getDepartmentIdByUserId(user_id);
    }

    /** 根据用户id查询用户系统 */
    public List<Integer> getSystemIdByUserId(Integer user_id){
        return userMapper.getSystemIdByUserId(user_id);
    }

    /** 根据显示名称查询用户id */
    public Integer getUserIdByDisplayName(String displayName){
        return userMapper.getUserIdByDisplayName(displayName);
    }
	
}
