package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author chh
 * @date 2022/1/22 12:03
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lend extends DeviceDocument{
    private Integer id;
    private String document_id;
    private Integer device_user_id;
    private String use_time;
    private String use_reason;
    private Integer approve_type;
    private Integer document_status;
    private String description;

    private String username;
    private String approveName;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> devices;
    private String doc_status_description;

    private Integer system_id;
    private List<Role> roleList;

    public Lend(Integer id, String document_id, Integer device_user_id, String use_time, String use_reason, Integer approve_type, Integer document_status, String description,Integer system_id){
        this.id = id;
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.device_user_id = device_user_id;
        super.setUser_id(device_user_id);
        this.use_time = use_time;
        this.use_reason = use_reason;
        this.approve_type = approve_type;
        this.description = description;
        this.document_status = document_status;
        this.system_id = system_id;
    }
    public Lend(String document_id, Integer device_user_id, String use_time, String use_reason, Integer approve_type, Integer document_status,String description,Integer system_id){
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.device_user_id = device_user_id;
        super.setUser_id(device_user_id);
        this.use_time = use_time;
        this.use_reason = use_reason;
        this.approve_type = approve_type;
        this.description = description;
        this.document_status = document_status;
        this.system_id = system_id;
    }
    // 这个构造函数前端传入
    public Lend(String document_id, Integer device_user_id, String use_time, String use_reason, Integer approve_type, Integer document_status,String description, Integer system_id,List<DocumentDevice> documentDeviceList){
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.device_user_id = device_user_id;
        super.setUser_id(device_user_id);
        this.use_time = use_time;
        this.use_reason = use_reason;
        this.approve_type = approve_type;
        this.description = description;
        this.document_status = document_status;
        this.system_id = system_id;
        this.documentDeviceList = documentDeviceList;
        super.setDocumentDeviceList(documentDeviceList);
    }

    @Override
    public Integer getUser_id() {
        return this.getDevice_user_id();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
        this.username = username;
    }

}
