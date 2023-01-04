package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Permission;
import com.example.dakudemo.entity.UrlFilter;
import com.example.dakudemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 11:05
 */
@Repository
@Mapper
public interface UserMapper {

    /**通过id查询用户信息**/
    public String getUserNameById(Integer id);
    /**通过id查询用户displayName**/
    public String getDisplayNameById(Integer id);
	 /*chh add*/
    /**通过用户名查询用户**/
    public User getUserByUserName(String user_name);
    /**注册添加用户**/
    public boolean addUser(User user);
    /**通过用户id删除用户**/
    public boolean deleteUserById(Integer id);

//    /**通过用户名删除用户**/
//    public boolean deleteUserByUserName(String user_name);
    /**通过用户id更新用户**/
    public boolean updateUserById(Integer id);
//    /**通过用户名删除用户**/
//    public boolean deleteUserByUserName(String user_name);

    /**通过用户名查询角色**/
    public User getRolesByUserName(String user_name);

    /**根据role_id查询权限集合**/
    public List<Permission> getPermsByRoleId(Integer roleId);

    /**查询所有角色和对应权限**/
    public List<UrlFilter> getAllUrlFilters();

    /**为指定用户id的用户添加角色**/
    public boolean addRoleForUserById(@Param("user_id") Integer user_id, @Param("role_id") Integer role_id);

    /**查询role表获取角色对应id**/
    public Integer getRoleIdByRoleName(String r_name);

    /**通过用户id查询用户名**/
    public Integer getUserIdByUserName(String user_name);

    /**通过department名称查询对应department_id**/
    public Integer getDepartmentIdByDepartmentName(String department_name);

    /**通过department_id查询对应department_name**/
    public String getDepartmentNameByDepartmentId(Integer id);

    /**添加一个部门**/
    public boolean addDepartment(String department_name);

    /**查询用户所在部门id**/
    public Integer getDepartmentIdByUserId(Integer user_id);
    /**根据displayname查询用户所在部门id**/
    public Integer getDepartmentIdByDispalyName(String displayName);

    /** 根据用户id查询用户系统 */
    public List<Integer> getSystemIdByUserId(Integer user_id);

    /** 根据显示名称查询用户id */
    public Integer getUserIdByDisplayName(String displayName);
}
