package com.example.dakudemo.entity;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */

public class RepairCategory {
    private Integer id;
    private String category;
    public RepairCategory(){

    }

    public RepairCategory(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
