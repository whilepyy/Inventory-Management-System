package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Inout;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 11:05
 */
@Repository
@Mapper
public interface OutApprovalMapper {

    /**删除审批*/
    public Boolean deleteOutApproval(Integer id);

    /**修改审批状态*/
    public Boolean updateOutApproval(@Param("id") Integer id,@Param("approve_status") String approve_status,@Param("approve_time") String approve_time);

    /**查询出库审批列表*/
    public List<Inout> getOutApprovalList(@Param("approve_status") String approve_status,@Param("approve_person_id") Integer approve_person_id);
    public List<Inout> getOutApprovalListAll(@Param("approve_status") String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
