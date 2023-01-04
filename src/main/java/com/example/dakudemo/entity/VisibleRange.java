package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @author:chh
 * @Date:2022-05-01-11:54
 * @Description:角色对应的可视范围
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisibleRange {
    private Integer id;
    private Integer role_id;
    private boolean system;
    private boolean department;
    private boolean all;
}
