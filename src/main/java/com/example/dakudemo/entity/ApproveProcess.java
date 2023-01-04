package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @author:chh
 * @Date:2022-05-01-11:13
 * @Description:审批流程节点实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveProcess {
    private Integer id;
    private Integer approve_type;
    private Integer approve_node;
    private Integer role_id;
}
