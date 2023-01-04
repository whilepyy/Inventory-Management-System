package com.example.dakudemo.entity;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */

public class RepairCause {
    private Integer id;
    private String cause;
    public RepairCause(){

    }

    public RepairCause(String cause) {
        this.cause = cause;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
