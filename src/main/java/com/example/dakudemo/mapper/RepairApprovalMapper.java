package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Repair;
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
public interface RepairApprovalMapper {

    /**删除报检报修*/
    public Boolean deleteRepairApproval(Integer id);

    /**修改报检报修审批状态*/
    public Boolean updateRepairApproval(@Param("id") Integer id,@Param("approve_status") String approve_status,@Param("approve_time") String approve_time);

    /**查询报检报修审批列表*/
    public List<Repair> getRepairApprovalList(@Param("approve_status") String approve_status, @Param("approve_person_id") Integer approve_person_id);
    public List<Repair> getRepairApprovalListAll(@Param("approve_status") String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
