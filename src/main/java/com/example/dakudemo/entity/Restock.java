package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.util.List;

/*
 * @author:chh
 * @Date:2022-09-04-10:19
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restock extends DeviceDocument{
    private Integer id;
    private String document_id;
    private String check_repair_document_id;
    private String check_repair_result;
    private String restock_time;
    private Integer document_person_id;
    private String description;

    private String document_person_name;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> device;

    private String doc_status_description;
    private Integer document_status;

    /* 前端传入构造函数 */
    public Restock(String document_id, String check_repair_document_id, String check_repair_result, Integer document_status,
                   String restock_time, String description, List<DocumentDevice> documentDeviceList){
        this.document_id = document_id;
        this.check_repair_document_id = check_repair_document_id;
        this.check_repair_result = check_repair_result;
        this.restock_time = restock_time;
        this.document_status = document_status;
        this.description = description;
        this.documentDeviceList = documentDeviceList;
        super.setDocument_id(document_id);
        //super.setUser_id(document_person_id);
        super.setDocumentDeviceList(documentDeviceList);
    }

    /* 后端返回构造函数 */
    public Restock(Integer id, String document_id, String check_repair_document_id, String check_repair_result, String restock_time, Integer document_person_id, Integer document_status, String description){
        this.id = id;
        this.document_id = document_id;
        this.check_repair_document_id = check_repair_document_id;
        this.check_repair_result = check_repair_result;
        this.restock_time = restock_time;
        this.document_person_id = document_person_id;
        super.setUser_id(document_person_id);
        this.document_status = document_status;
        this.description = description;
    }


}
