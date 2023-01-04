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
public class DocumentDevice {
    private Integer id;
    private String document_id;
    private String device_id;
    private Integer device_number;
    private String device_status;

    private String device_name;
    private Integer device_rest_number;
    private boolean isEnough;

    public DocumentDevice(Integer id,String document_id, String device_id, Integer device_number, String device_status){
        this.id = id;
        this.document_id = document_id;
        this.device_id = device_id;
        this.device_number = device_number;
        this.device_status = device_status;
    }
    public DocumentDevice(String document_id, String device_id, Integer device_number, String device_status) {
        this.document_id = document_id;
        this.device_id = device_id;
        this.device_number = device_number;
        this.device_status = device_status;
    }
}
