package com.example.dakudemo.mapper;


import com.example.dakudemo.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChangeMapper {

//role_permission表
    @Select("select count(*) from role_permission where role_id = #{role_id} and permission_id = #{permission_id}")
    int RP_exist(@Param("role_id") Integer role_id,@Param("permission_id") Integer permission_id);

    int RPinsert(@Param("role_id") Integer role_id,
                 @Param("permission_id") Integer permission_id);
//
//
    //删整个role
    @Delete("delete from role_permission where role_id = #{role_id}")
    Integer role_deleteByRoleId(@Param("role_id") Integer role_id);
//
//
//
    //删role的某个权限
    @Delete("delete from role_permission where role_id = #{role_id} and permission_id = #{permission_id}")
    Integer role_permission_deleteByRoleID(@Param("role_id") Integer role_id,
                                           @Param("permission_id") Integer permission_id);
//
//role表
    //增改
    @Select("select count(*) from `role` where r_name = #{r_name}")
    int role_exist(String r_name);

    int role_insert(@Param("r_name") String r_name,
                    @Param("description") String description);

    int role_update(@Param("r_name") String r_name,
                    @Param("description") String description);
//
    //查询
    List<Role_chg> role_find(@Param("id") Integer id,
                             @Param("r_name") String r_name);
//    //删除
    @Delete("delete from role where id = #{id}")
    int roledeleteById(@Param("id") Integer id);


//user表
    //查询功能
//    @Select("SELECT * from user")
//    List<User> userfindAll();
//
//    @Select("SELECT * from user where id = #{id}")
//    List<User> userfindID(Integer id);

    @Select("select count(*) from user where user_name = #{name}")
    int username_exist(String name);

    List<User_chg> userfind(@Param("user_id") Integer user_id,
                            @Param("user_name") String user_name,
                            @Param("displayName") String displayName,
                            @Param("system_id") Integer system_id,
                            @Param("department_id") Integer department_id);
    //增改
    @Insert("INSERT into user(id, user_name, password, displayName, department_id) VALUES (#{id}, #{user_name}, #{password}, #{displayName}, #{department_id})")
    int userinsert(User_chg user);




    int userupdate(User_chg user);
    //删除
    @Delete("delete from user where id = #{id}")
    Integer userdeleteById(@Param("id") Integer id);



//system表
    //增改
    @Insert("insert into `system`(system_name,description) VALUES (#{system_name},#{description})")
    int systeminsert(System_chg system);

    int systemupdate(System_chg system);

    @Select("select count(*) from `system` where system_name = #{system_name}")
    Integer sysname_exist(String system_name);

    //查
    List<System_chg> system_find(@Param("system_id") Integer system_id,
                                 @Param("system_name") String system_name,
                                 @Param("department_id") Integer department_id);

//    @Select("select id from `system` where system_name = #{system_name}")
//    Integer system_id(String system_name);
//    @Select("select system_name from `system` where id = #{id}")
//    String system_name(Integer id);

    //删
    @Delete("delete from `system` where id = #{id}")
    Integer systemdeleteById(@Param("id") Integer id);

//    @MapKey("id")
//    Map<Integer,Object> system_findAll();



//department表
    @Select("select count(*) from `department` where department_name = #{name}")
    int department_name_exist(String name);

    int department_insert(@Param("name") String name,
                          @Param("description") String description);

    int department_update(@Param("name") String name,
                          @Param("description") String description);

    @Select("select `id` from `department` where `name` = #{name}")
    Integer department_id(String name);

    @Select("select `name` from `department` where `id` = #{id}")
    String department_name(Integer id);

    List<Department_chg> department_findAll();

    @Delete("delete from department where id = #{id}")
    Integer departmentdeleteById(@Param("id") Integer id);


//department_system表
    //增改
    @Select("select count(*) from department_system where system_id = #{system_id}")
    int system_id_exist(Integer system_id);

    int DSinsert(@Param("department_id") Integer department_id,
                 @Param("system_id") Integer systerm_id);

    int DSupdate(@Param("department_id") Integer department_id,
                 @Param("system_id") Integer systerm_id);

//    //查
//    @Select("select department_id from department_system where system_id = #{system_id}")
//    int sys_find_dep(Integer system_id);
//
//    @Select("select system_id from department_system where department_id = #{department_id}")
//    List<Integer> dep_find_sys(Integer department_id);

    //删
    @Delete("delete from department_system where system_id = #{system_id}")
    Integer department_system_deleteBysystemId(@Param("system_id") Integer system_id);

    @Delete("delete from department_system where department_id = #{department_id}")
    Integer department_system_deleteBydepartmentId(@Param("department_id") Integer department_id);

//user_role_system_department表
    //增改

    int ursd_insert(@Param("user_id") Integer user_id,
                    @Param("URSD") Role_System_Department_chg URSD);

    int ursd_update(@Param("user_id") Integer user_id,
                    @Param("URSD") Role_System_Department_chg URSD);

    @Select("select count(*) from user_role_system_department where user_id = #{user_id} " +
            "and role_id = #{URSD.role_id} and system_id = #{URSD.system_id} and" +
            " department_id = #{URSD.department_id}")
    int user_ursd_exist(@Param("user_id") Integer user_id,
                        @Param("URSD") Role_System_Department_chg URSD);

    @Delete("delete from user_role_system_department where user_id = #{user_id}")
    Integer URSDdeleteById(Integer user_id);

    @MapKey("id")
    Map<Integer,Object> URSD_findid(Integer user_id);

    @Select("select id from `system` where system_name = #{system_name}")
    Integer system_find_id(String system_name);


    @Select("select id from role where r_name = #{name}")
    Integer role_find_id(String name);

    @Select("select id from user where user_name = #{name}")
    Integer user_find_id(String name);


//name_find
    @Select("select r_name from `role` where id = #{role_id}")
    String role_find_name(Integer role_id);

    @Select("select system_name from `system` where id = #{system_id}")
    String system_find_name(Integer system_id);

    @Select("select department_name from department where id = #{department_id} ")
    String departmetent_find_name(Integer department_id);

}