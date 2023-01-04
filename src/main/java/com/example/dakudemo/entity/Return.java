package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author chh
 * @date 2022/1/22 12:08
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return extends DeviceDocument{
    private Integer id;
    private String document_id;
    private Integer return_person_id;
    private String borrow_document_id;
    private String return_time;
    private String description;

    private String return_person_name;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> devices;

//    public Return(Map<String,String> Params) {
//        this.id = null;
//        this.document_id = Params.get("document_id");
//        this.return_person_id = Params.get("return_person_id");
//        this.borrow_document_id = Params.get("borrow_document_id");
//        this.return_time = Params.get("return_time");
//
//        this.return_person_name = null;
//        this.documentDeviceList = null;
//        this.device = null;
//    }

    // 前端传入字段
    public Return(String document_id, Integer return_person_id, String borrow_document_id, String return_time, String return_person_name, String description, List<DocumentDevice> documentDeviceList) {
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.return_person_id = return_person_id;
        this.borrow_document_id = borrow_document_id;
        this.return_time = return_time;
        this.description = description;
        this.documentDeviceList = documentDeviceList;
        super.setDocumentDeviceList(documentDeviceList);
        this.return_person_name = return_person_name;
    }

    // 后端返回字段
    public Return(Integer id, String document_id, Integer return_person_id, String borrow_document_id, String return_time, String return_person_name, String description) {
        this.id = id;
        this.document_id = document_id;
        super.setDocument_id(document_id);
        this.return_person_id = return_person_id;
        this.borrow_document_id = borrow_document_id;
        this.return_time = return_time;
        this.return_person_name = return_person_name;
        this.description = description;
    }


    @Override
    public Integer getUser_id() {
        return this.return_person_id;
    }

    @Override
    public Integer getApprove_person_id() {
        return null;
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
//        setReturn_person_name(username);
    }
}