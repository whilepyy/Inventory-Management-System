package com.example.dakudemo.entity;

/*
 * @author:chh
 * @Date:2022-04-28-14:29
 * @Description:审批
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Approve {
    private Integer id;
    private String document_id;
    private Integer approve_type;
    private Integer document_type;
    private Integer user_id;
    private Integer system_id;
    private Integer approve_person_id;
    private Integer approve_node;
    private Integer approve_status;
    private String approve_time;
    private String approve_suggestion;

    private String user_name;
    private String approver_name;
    private String system_name;
    private Inout inout;
    private Lend lend;
    private Repair repair;
    private Restock restock;
    private Scrap scrap;
    private DeviceDocument doc;

    public Approve(Integer id, String document_id, Integer approve_type, Integer document_type, Integer user_id, Integer system_id, Integer approve_person_id, Integer approve_node, Integer approve_status, String approve_time, String approve_suggestion) {
        this.id = id;
        this.document_id = document_id;
        this.approve_type = approve_type;
        this.document_type = document_type;
        this.user_id = user_id;
        this.system_id = system_id;
        this.approve_person_id = approve_person_id;
        this.approve_node = approve_node;
        this.approve_status = approve_status;
        this.approve_time = approve_time;
        this.approve_suggestion = approve_suggestion;
    }

    public Approve(String document_id, Integer approve_type, Integer document_type, Integer user_id, Integer system_id, Integer approve_node, Integer approve_status, String approve_suggestion) {
        this.document_id = document_id;
        this.approve_type = approve_type;
        this.document_type = document_type;
        this.user_id = user_id;
        this.system_id = system_id;
        this.approve_node = approve_node;
        this.approve_status = approve_status;
        this.approve_suggestion = approve_suggestion;
    }

    public Approve(Integer id, String document_id, Integer approve_type, Integer document_type, Integer user_id, Integer system_id, Integer approve_node, Integer approve_status, String approve_suggestion) {
        this.id = id;
        this.document_id = document_id;
        this.approve_type = approve_type;
        this.document_type = document_type;
        this.user_id = user_id;
        this.system_id = system_id;
        this.approve_node = approve_node;
        this.approve_status = approve_status;
        this.approve_suggestion = approve_suggestion;
    }
}
