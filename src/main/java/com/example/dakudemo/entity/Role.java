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
public class Role implements Serializable{
    private Integer id;
    private String name;
    private String description;

    private Integer system_id;
    private String system_name;
    private Integer department_id;
    private String department_name;

    public Role(Integer id, String name, Integer system_id, String system_name, Integer department_id, String department_name) {
        this.id = id;
        this.name = name;
        this.system_id = system_id;
        this.system_name = system_name;
        this.department_id = department_id;
        this.department_name = department_name;
    }

    public Role(Integer id, Integer system_id, Integer department_id) {
        this.id = id;
        this.system_id = system_id;
        this.department_id = department_id;
    }
}
