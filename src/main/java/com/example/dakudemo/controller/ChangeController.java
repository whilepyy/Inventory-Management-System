package com.example.dakudemo.controller;


import com.example.dakudemo.entity.Department_chg;
import com.example.dakudemo.entity.Role_chg;
import com.example.dakudemo.entity.System_chg;
import com.example.dakudemo.entity.User_chg;
import com.example.dakudemo.mapper.ChangeMapper;
import com.example.dakudemo.service.ChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/daku/userInfo")
public class ChangeController {
    @Autowired
    private ChangeMapper changeMapper;
    @Autowired
    private ChangeService changeService;

//角色管理
    //添加角色
    @PostMapping("/role")
    public Integer rolesave(@RequestBody Role_chg role) {
        return changeService.rolesave(role);
    }


    @GetMapping("/role/find")
    public List<Role_chg> rolefind(@RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String r_name){
        return changeMapper.role_find(id,r_name);
    }


    //删除整个role
    @DeleteMapping("/role/role_delete")
    public Integer roledelete(@RequestParam Integer id) {
        changeMapper.role_deleteByRoleId(id);
        return changeMapper.roledeleteById(id);
    }
//
    //删除某个role的某个permission
    @DeleteMapping("/role/permission_delete")
    public Integer permissiondelete(@RequestParam Integer id,@RequestParam Integer permission_id) {
        return changeMapper.role_permission_deleteByRoleID(id,permission_id);

    }










//用户管理
    //新增和修改
    @PostMapping("/user")
    public Integer usersave(@RequestBody User_chg user) {
        return changeService.usersave(user);
    }

    //根据模糊的user_name查询
    @GetMapping("/user/find")
    public List<User_chg> userfind(@RequestParam(required = false) Integer user_id ,
                                   @RequestParam(required = false) String user_name,
                                   @RequestParam(required = false) String displayName,
                                   @RequestParam(required = false) Integer system_id,
                                   @RequestParam(required = false) Integer department_id) {

        List<User_chg> all = changeService.user_find(user_id,user_name,displayName,system_id,department_id);

        return all;
    }


    //根据具体的user_id删除
    @DeleteMapping("/user/{id}")
    public Integer userdelete(@PathVariable Integer id) {
         changeMapper.URSDdeleteById(id);
        return changeMapper.userdeleteById(id);
    }






//部门管理
    //增改(如果部门名称已经存在，则不插入，若不存在则插入，插入成功返回 1，插入失败返回 0)
    @PostMapping("/department")
    public Integer department_save(@RequestBody Department_chg department) {
        return changeService.departmentsave(department);
    }

//    //根据department_id查department_name
//    @GetMapping("/department/findname")
//    public String department_get_name(@RequestParam Integer id) {
//        return changeMapper.department_name(id);
//    }

    //查询并返回所有的部门信息(List<>)
    @GetMapping("/department/find")
    public List<Department_chg> departmentfindall(){
        List<Department_chg>  all = changeMapper.department_findAll();
//        Department_chg temp;
//        if(all != null){
//            temp = all.get(0);
//            temp.setDepartment_id(90);
//        }
        return all;
    }

    //删
    @DeleteMapping("/department/{id}")
    public Integer department_delete(@PathVariable Integer id) {
        changeMapper.department_system_deleteBydepartmentId(id);
        return changeMapper.departmentdeleteById(id);
    }





//系统管理
    //增改
    @PostMapping("/system")
    public Integer system_save(@RequestBody System_chg system) {
        return changeService.systemsave(system);
    }
    //根据system_name查id
    @GetMapping("/system/find")
    public List<System_chg> system_find(@RequestParam(required = false) Integer system_id,
                                        @RequestParam(required = false) String system_name,
                                        @RequestParam(required = false) Integer department_id) {
        return changeMapper.system_find(system_id,system_name,department_id);
    }

//    //根据system_id查system_name
//    @GetMapping("/system/findname")
//    public String system_get_name(@RequestParam Integer id) {
//        return changeMapper.system_name(id);
//    }

//    //显示所有系统(返回map)
//    @GetMapping("/system/findall")
//    public Map<Integer,Object> systemfindall(){
//        Map<Integer, Object> res = new HashMap<>();
//        res = changeMapper.system_findAll();
//        return res;
//    }

    //删
    @DeleteMapping("/system/{id}")
    public Integer system_delete(@PathVariable Integer id) {
        int i = changeMapper.department_system_deleteBysystemId(id);
        return changeMapper.systemdeleteById(id);
    }



//user_role_system_department表
    //增改
//    @GetMapping("/URSD")
//    public Integer URSD_save(@RequestParam Integer user_id,
//                             @RequestParam Integer role_id,
//                             @RequestParam Integer system_id,
//                             @RequestParam Integer department_id){
//        //return changeMapper.system_id_exist(system_id);
//        //return changeMapper.DSinsert(system_id,department_id);
//        return changeService.ursdsave(user_id, role_id, system_id, department_id);
//    }

    @GetMapping("/URSD/findid")
    public Map<Integer,Object> URSDfindid(@RequestParam Integer user_id){
        Map<Integer, Object> res = new HashMap<>();
        res = changeMapper.URSD_findid(user_id);
        return res;

    }

    //删除
    @DeleteMapping("/URSD/{user_id}")
    public Integer URSD_delete(@PathVariable Integer user_id) {
        return changeMapper.URSDdeleteById(user_id);
    }



}



