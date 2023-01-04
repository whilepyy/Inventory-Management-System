package com.example.dakudemo.service;

import com.example.dakudemo.entity.Return;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface ReturnApprovalService {

    /**查询归还单审批列表信息**/
    List<Return> getReturnApprovalList(String approve_status, Integer approve_person_id);
    List<Return> getReturnApprovalListAll(String approve_status);
    /**修改归还单审批**/
    public Boolean updateReturnApproval(Integer id,String approve_status,String approve_time);

    /**删除归还单审批**/
    public Boolean deleteReturnApproval(Integer id);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
