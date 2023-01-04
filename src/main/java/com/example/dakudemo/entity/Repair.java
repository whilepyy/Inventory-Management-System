package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repair extends DeviceDocument{
    private Integer id;
    private String document_id;
    private Integer check_repair_category_id;
    private Integer check_repair_cause_id;
    private String fault_description;
    private String repair_company;
    private Integer document_person_id;
    private String description;
    private String repair_person_name;
    private String check_repair_result;
    private String document_person_name;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> device;

    private String doc_status_description;
    private Integer document_status;

    // 数据库查询构造
    public Repair(Integer id, String document_id, Integer check_repair_category_id, Integer check_repair_cause_id,
                  String fault_description, String repair_company, Integer document_person_id, String description,
                  String repair_person_name, String check_repair_result, Integer document_status) {
        this.id = id;
        this.document_id = document_id;
        this.check_repair_category_id = check_repair_category_id;
        this.check_repair_cause_id = check_repair_cause_id;
        this.fault_description = fault_description;
        this.repair_company = repair_company;
        this.document_person_id = document_person_id;
        this.description = description;
        this.repair_person_name = repair_person_name;
        this.check_repair_result = check_repair_result;
        this.document_status = document_status;
    }

    // 前端传入构造函数
    public Repair(String document_id, Integer check_repair_category_id, Integer check_repair_cause_id,
                  String fault_description, String repair_company, Integer document_person_id,
                  String description, String repair_person_name, String check_repair_result,
                  List<DocumentDevice> documentDeviceList, Integer document_status) {
        this.document_id = document_id;
        this.check_repair_category_id = check_repair_category_id;
        this.check_repair_cause_id = check_repair_cause_id;
        this.fault_description = fault_description;
        this.repair_company = repair_company;
        this.document_person_id = document_person_id;
        this.description = description;
        this.repair_person_name = repair_person_name;
        this.check_repair_result = check_repair_result;
        this.documentDeviceList = documentDeviceList;
        super.setDocumentDeviceList(documentDeviceList);
        this.document_status = document_status;
    }

    @Override
    public void setUsername(String username){
        this.document_person_name = username;
    }

    @Override
    public Integer getUser_id(){
        return this.document_person_id;
    }
}
