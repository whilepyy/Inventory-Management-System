package com.example.dakudemo.service;

import com.example.dakudemo.entity.Scrap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/2 9:56
 */
public interface ScrapApprovalService {
    /**删除审批*/
    public Boolean deleteScrapApproval(Integer id);

    /**修改审批状态*/
    public Boolean updateScrapApproval(Integer id, String approve_status, String approve_time);

    /**查询报废审批列表*/
    public List<Scrap> getScrapApprovalList(String approve_status, Integer approve_person_id);
    public List<Scrap> getScrapApprovalListAll(String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);
}
