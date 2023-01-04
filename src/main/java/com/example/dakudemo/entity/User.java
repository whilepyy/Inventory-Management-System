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
public class User implements Serializable {
    private Integer id;
    private String user_name;
    private String password;
    private String displayName;
    private String create_time;
    private Integer department_id;
    private String description;
    private String token;

    //½ÇÉ«¼¯ºÏ
    private List<Role> roles;

    public User(Integer id, String username, List<Role> roles) {
        this.id = id;
        this.user_name = username;
        this.roles = roles;
    }

    public User(Integer id, String user_name, String password, String displayName, String create_time, Integer department_id) {
        this.id = id;
        this.user_name = user_name;
        this.password = password;
        this.displayName = displayName;
        this.create_time = create_time;
        this.department_id = department_id;
    }


}
