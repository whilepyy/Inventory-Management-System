package com.example.dakudemo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Department_chg implements Serializable {

    private Integer department_id;
    private String department_name;
    private String description;

}
