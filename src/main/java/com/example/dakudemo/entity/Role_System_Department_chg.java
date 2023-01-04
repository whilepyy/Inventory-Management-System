package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role_System_Department_chg implements Serializable {
    private Integer user_id;
    private String user_name;
    private Integer role_id;
    private String role_name;
    private Integer system_id;
    private String system_name;
    private Integer department_id;
    private String department_name;

}

