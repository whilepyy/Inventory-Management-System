package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chh
 * @date 2022/2/17 11:48
 * 此实体用于描述通用返回类型
 */
@Data
@NoArgsConstructor
public class Result {
    private Boolean isSuccess;
    private Integer code;
    private String msg;
    private List<?> list;
    private Object object;
}
