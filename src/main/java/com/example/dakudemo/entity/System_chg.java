package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class System_chg implements Serializable {
    private Integer system_id;
    private String system_name;
    private String description;

    private Integer department_id;

    private String department_name;

    public System_chg(Integer system_id, String system_name, Integer department_id, String department_name) {
        this.system_id = system_id;
        this.system_name = system_name;
        this.department_id = department_id;
        this.department_name = department_name;
    }

    public System_chg(Integer system_id, Integer department_id) {
        this.system_id = system_id;
        this.department_id = department_id;
    }
}
