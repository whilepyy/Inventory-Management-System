package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCategory {
    private Integer id;
    private String category;
    private String category_nu;
}
