package com.example.dakudemo.service;

import com.example.dakudemo.entity.Inout;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface OutApprovalService {

    /**查询out列表信息**/
    List<Inout> getOutApprovalList(String approve_status,Integer approve_person_id);
    List<Inout> getOutApprovalListAll(String approve_status);
    /**修改out**/
    public Boolean updateOutApproval(Integer id,String approve_status,String approve_time);

    /**删除出库申请**/
    public Boolean deleteOutApproval(Integer id);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
