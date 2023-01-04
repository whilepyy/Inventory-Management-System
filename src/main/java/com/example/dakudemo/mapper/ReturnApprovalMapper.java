package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Return;
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
public interface ReturnApprovalMapper {

    /**删除归还单子*/
    public Boolean deleteReturnApproval(Integer id);

    /**修改归还单审批状态*/
    public Boolean updateReturnApproval(@Param("id") Integer id,@Param("approve_status") String approve_status,@Param("approve_time") String approve_time);

    /**查询归还单审批列表*/
    public List<Return> getReturnApprovalList(@Param("approve_status") String approve_status, @Param("approve_person_id") Integer approve_person_id);
    public List<Return> getReturnApprovalListAll(@Param("approve_status") String approve_status);
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id);

}
