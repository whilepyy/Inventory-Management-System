package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Lend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chh
 * @date 2022/1/25 11:29
 */
@Repository
@Mapper
public interface LendApprovalMapper {
    /**删除审批*/
    public Boolean deleteLendApproval(Integer id);

    /**修改审批状态*/
    public Boolean updateLendApproval(@Param("id") Integer id, @Param("approve_status") String approve_status, @Param("approve_time") String approve_time);

    /**查询出库审批列表*/
    public List<Lend> getLendApprovalList(@Param("approve_status") String approve_status, @Param("approve_person_id") Integer approve_person_id);
    public List<Lend> getLendApprovalListAll(@Param("approve_status") String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);
}
