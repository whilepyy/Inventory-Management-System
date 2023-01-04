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
public class Role_chg implements Serializable{
    private Integer id;
    private String name;
    private String description;

    private List<Role_Permission> role_permission;

}
