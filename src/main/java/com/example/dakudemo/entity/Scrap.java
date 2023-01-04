package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author chh
 * @date 2022/2/28 22:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scrap extends DeviceDocument{
    /**数据库数据属性**/
    private Integer id;
    private String document_id;
    private Integer document_person_id;
    private String scrap_reason;
    private String process_way;
    private String description;

    /**额外的属性**/
    private String username;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> devices;

    private String doc_status_description;
    private Integer document_status;

    @Override
    public Integer getUser_id() {
        return this.document_person_id;
    }

    // 后端返回字段
    public Scrap(Integer id, String document_id, Integer document_person_id, String scrap_reason, String process_way, Integer document_status, String description) {
        this.id = id;
        this.document_id = document_id;
        this.document_person_id = document_person_id;
        this.scrap_reason = scrap_reason;
        this.process_way = process_way;
        this.document_status = document_status;
        this.description = description;
    }

    // 前端传入字段
    public Scrap(String document_id, Integer document_person_id, String scrap_reason, String process_way, Integer document_status, String description,List<DocumentDevice> documentDeviceList) {
        this.document_id = document_id;
        this.document_person_id = document_person_id;
        this.scrap_reason = scrap_reason;
        this.process_way = process_way;
        this.description = description;
        this.document_status = document_status;
        this.documentDeviceList = documentDeviceList;
    }
}
