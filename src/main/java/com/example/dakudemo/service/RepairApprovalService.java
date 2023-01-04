package com.example.dakudemo.service;

import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Repair;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface RepairApprovalService {

    /**查询报检报修列表信息**/
    List<Repair> getRepairApprovalList(String approve_status, Integer approve_person_id);
    List<Repair> getRepairApprovalListAll(String approve_status);
    /**修改报检报修**/
    public Boolean updateRepairApproval(Integer id,String approve_status,String approve_time);

    /**删除报检报修申请**/
    public Boolean deleteRepairApproval(Integer id);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
