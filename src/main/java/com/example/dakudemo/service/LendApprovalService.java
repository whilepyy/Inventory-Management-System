package com.example.dakudemo.service;

import com.example.dakudemo.entity.Lend;

import java.util.List;

/**
 * @author chh
 * @date 2022/1/25 12:05
 */
public interface LendApprovalService {
    /**查询Lend列表信息**/
    public List<Lend> getLendApprovalList(String approve_status, Integer approve_person_id);
    public List<Lend> getLendApprovalListAll(String approve_status);
    /**修改lend**/
    public Boolean updateLendApproval(Integer id,String approve_status,String approve_time);

    /**删除出库申请**/
    public Boolean deleteLendApproval(Integer id);

    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);
}
