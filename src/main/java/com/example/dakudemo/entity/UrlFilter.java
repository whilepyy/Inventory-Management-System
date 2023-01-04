package com.example.dakudemo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chh
 * @date 2022/2/18 12:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlFilter implements Serializable{
    private Integer id;
    private String roleName;
    private String permName;
    private String url;
}
