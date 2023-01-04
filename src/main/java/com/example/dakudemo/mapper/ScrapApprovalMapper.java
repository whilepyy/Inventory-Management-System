package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Scrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 16:53
 */
@Repository
@Mapper
public interface ScrapApprovalMapper {
    /**删除审批*/
    public Boolean deleteScrapApproval(Integer id);

    /**修改审批状态*/
    public Boolean updateScrapApproval(@Param("id") Integer id, @Param("approve_status") String approve_status, @Param("approve_time") String approve_time);

    /**查询报废审批列表*/
    public List<Scrap> getScrapApprovalList(@Param("approve_status") String approve_status, @Param("approve_person_id") Integer approve_person_id);
    public List<Scrap> getScrapApprovalListAll(@Param("approve_status") String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);
}
