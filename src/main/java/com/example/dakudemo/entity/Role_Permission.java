package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor




public class Role_Permission {

    private Integer permission_id;
    private String p_name;
    private String url;
}
