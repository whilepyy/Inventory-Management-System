package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inout extends DeviceDocument{
    private Integer id;
    private String document_id;
    private Integer document_category_id;
    private Integer buy_use_person_id;
    private String buy_use_time;
    private String buy_use_reason;
    private Integer approve_type;
    private Integer document_status;
    private String description;
    private String buy_person_name;

    private String username;
    private String approveName;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> devices;
    private Device device;
    private String doc_status_description;


    private Integer system_id;
    private List<Role> roleList;

    // 入库单数据库查询构造
    public Inout(Integer id,String document_id, Integer document_category_id, Integer buy_use_person_id, String buy_use_time, String buy_use_reason, Integer approve_type,Integer document_status, String description, String buy_person_name) {
        this.id = id;
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.document_category_id = document_category_id;
        this.buy_use_person_id = buy_use_person_id;
        super.setUser_id(buy_use_person_id);
        this.buy_use_time = buy_use_time;
        this.buy_use_reason = buy_use_reason;
        this.approve_type = approve_type;
        this.document_status = document_status;
        this.description = description;
        this.buy_person_name = buy_person_name;
    }
    // 出库单数据库查询构造
    public Inout(Integer id,String document_id, Integer document_category_id, Integer buy_use_person_id, String buy_use_time, String buy_use_reason, Integer approve_type,Integer document_status, String description) {
        this.id = id;
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.document_category_id = document_category_id;
        this.buy_use_person_id = buy_use_person_id;
        super.setUser_id(buy_use_person_id);
        this.buy_use_time = buy_use_time;
        this.buy_use_reason = buy_use_reason;
        this.approve_type = approve_type;
        this.document_status = document_status;
        this.description = description;
    }

    public Inout(String document_id, Integer document_category_id, Integer buy_use_person_id, String buy_use_time, String buy_use_reason, Integer approve_type, Integer document_status, String description) {
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.document_category_id = document_category_id;
        this.buy_use_person_id = buy_use_person_id;
        super.setUser_id(buy_use_person_id);
        this.buy_use_time = buy_use_time;
        this.buy_use_reason = buy_use_reason;
        this.approve_type = approve_type;
        this.document_status = document_status;
        this.description = description;
    }
    // 出库单前端传入构造函数
    public Inout(String document_id, Integer document_category_id, Integer buy_use_person_id, String buy_use_time, String buy_use_reason, Integer approve_type, Integer document_status,String description, Integer system_id, List<DocumentDevice> documentDeviceList) {
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.document_category_id = document_category_id;
        this.buy_use_person_id = buy_use_person_id;
        super.setUser_id(buy_use_person_id);
        this.buy_use_time = buy_use_time;
        this.buy_use_reason = buy_use_reason;
        this.approve_type = approve_type;
        this.description = description;
        this.system_id = system_id;
        this.documentDeviceList = documentDeviceList;
        super.setDocumentDeviceList(documentDeviceList);
        this.document_status = document_status;
    }
    // 入库单前端传入构造函数
    public Inout(String document_id, Integer document_category_id, Integer buy_use_person_id, String buy_person_name, String buy_use_time, String buy_use_reason, Integer approve_type,  String description, Integer system_id, List<DocumentDevice> documentDeviceList, Device device) {
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.document_category_id = document_category_id;
        this.buy_use_person_id = buy_use_person_id;
        super.setUser_id(buy_use_person_id);
        this.buy_use_time = buy_use_time;
        this.buy_use_reason = buy_use_reason;
        this.approve_type = approve_type;
        this.description = description;
        this.system_id = system_id;
        this.documentDeviceList = documentDeviceList;
        this.device = device;
        super.setDocumentDeviceList(documentDeviceList);
        this.buy_person_name = buy_person_name;
    }
    @Override
    public Integer getUser_id() {
        return this.getBuy_use_person_id();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
        this.username = username;
    }
}
