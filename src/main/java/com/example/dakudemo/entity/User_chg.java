package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_chg implements Serializable {
    private Integer id;
    private String user_name;
    private String password;
    private String displayName;
    private String create_time;
    private Integer department_id;
    private String token;

    //½ÇÉ«¼¯ºÏ
    private List<Role_System_Department_chg> user_role_system_department;

    public User_chg(Integer id, String username,List<Role_System_Department_chg> URSD) {
        this.id = id;
        this.user_name = username;
        this.user_role_system_department = URSD;
    }

    public User_chg( String user_name, String password, String displayName, List<Role_System_Department_chg> URSD) {
        this.user_name = user_name;
        this.password = password;
        this.displayName = displayName;
        this.user_role_system_department = URSD;
    }
}
