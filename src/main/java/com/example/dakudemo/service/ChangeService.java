package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import com.example.dakudemo.mapper.ChangeMapper;
import com.example.dakudemo.util.EncryptUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ChangeService {
    @Autowired
    private ChangeMapper changeMapper;


    public int ursdsave(Integer user_id,Role_System_Department_chg URSD) {
        if(changeMapper.user_ursd_exist(user_id,URSD) == 0) {
            return changeMapper.ursd_insert(user_id,URSD);
        }
        else {
            return 0;
        }
    }

    public int usersave(User_chg user) {
        String password;
        String name = user.getUser_name();
        List<Role_System_Department_chg> URSD= user.getUser_role_system_department();
        int error_judge = 1;
        if (changeMapper.username_exist(name) == 0) {
            // 判断密码是否为空
            if(ObjectUtils.isEmpty(user.getPassword())){
                password = EncryptUtils.getRandPassword();
            }else{
                password = user.getPassword();
            }
            user.setPassword(EncryptUtils.encrypt(user.getUser_name(), password));
            error_judge = changeMapper.userinsert(user);
            Integer user_id = changeMapper.user_find_id(name);
            if(URSD != null){
                for(int i = 0;i< URSD.size();i++){
                    error_judge = error_judge * ursdsave(user_id, URSD.get(i));
                }
            }
            return 1;
        } else {
            if(!(ObjectUtils.isEmpty(user.getDepartment_id()) && ObjectUtils.isEmpty(user.getDisplayName()))){
                error_judge = changeMapper.userupdate(user);
            }

            Integer user_id = changeMapper.user_find_id(name);
            if(URSD != null){
                for(int i = 0;i< URSD.size();i++){
                    error_judge = error_judge * ursdsave(user_id, URSD.get(i));
                }
            }
            return 1;
        }
    }

    public int DSsave(Integer department_id,Integer system_id) {
        if(changeMapper.system_id_exist(system_id) == 0) {
            return changeMapper.DSinsert(department_id,system_id);
        }
        else{
            return changeMapper.DSupdate(department_id,system_id);
        }
    }


    public int systemsave(System_chg system) {
        String system_name = system.getSystem_name();
        Integer department_id = system.getDepartment_id();
        int error_judge = 1;
        if (changeMapper.sysname_exist(system_name) == 0) {
            error_judge = changeMapper.systeminsert(system);
            Integer system_id = changeMapper.system_find_id(system_name);
            if(department_id != null)
                error_judge = error_judge*DSsave(department_id,system_id);
            return error_judge;

        } else {
            error_judge = changeMapper.systemupdate(system);
            Integer system_id = changeMapper.system_find_id(system_name);
            if(department_id != null)
                error_judge = error_judge*DSsave(department_id,system_id);
            return error_judge;
        }
    }

    public int RPsave(Integer role_id,Integer permission_id){
        if(changeMapper.RP_exist(role_id,permission_id) == 0){
            return changeMapper.RPinsert(role_id,permission_id);
        }
        else return 1;
    }


    public int rolesave(Role_chg role) {
        String name = role.getName();
        String description = role.getDescription();
        List<Role_Permission> RP = role.getRole_permission();

        int x,y;//用来控制返回值

        if(changeMapper.role_exist(name) == 0) {
            x = changeMapper.role_insert(name,description);
            y = 1;
            Integer role_id = changeMapper.role_find_id(name);
            if(RP != null){
                for(int i = 0;i< RP.size();i++){
                    y = y * RPsave(role_id, RP.get(i).getPermission_id());
                }
            }
            return x * y;
        }
        else {
            x = changeMapper.role_update(name,description);
            y = 1;
            Integer role_id = changeMapper.role_find_id(name);
            if(RP != null){
                for(int i = 0;i< RP.size();i++){
                    y = y * RPsave(role_id, RP.get(i).getPermission_id());
                }
            }
            return x*y;
        }
    }


    public int departmentsave(Department_chg department) {
        String name = department.getDepartment_name();
        String description = department.getDescription();
        if(changeMapper.department_name_exist(name) == 0) {
            return changeMapper.department_insert(name,description);
        }
        else {
            return changeMapper.department_update(name,description);
        }
    }

    public List<User_chg> user_find(Integer user_id,
                                    String user_name,
                                    String displayName,
                                    Integer system_id,
                                    Integer department_id) {
        List<User_chg> temp = changeMapper.userfind(user_id,user_name,displayName,system_id,department_id);
        if(temp !=null){
            for(int i = 0;i<temp.size();i++){
                List<Role_System_Department_chg> ursd = temp.get(i).getUser_role_system_department();
                if(ursd !=null){
                    for(int j = 0;j<ursd.size();j++){
                        Integer role_id1 = ursd.get(j).getRole_id();
                        Integer system_id1 = ursd.get(j).getSystem_id();
                        Integer department_id1 = ursd.get(j).getDepartment_id();
                        ursd.get(j).setRole_name(changeMapper.role_find_name(role_id1));
                        ursd.get(j).setSystem_name(changeMapper.system_find_name(system_id1));
                        ursd.get(j).setDepartment_name(changeMapper.departmetent_find_name(department_id1));
                    }
                }
            }
        }

        return temp;

    }


}
