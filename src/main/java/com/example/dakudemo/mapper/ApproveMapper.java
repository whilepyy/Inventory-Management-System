package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Approve;
import com.example.dakudemo.entity.ApproveProcess;
import com.example.dakudemo.entity.Role;
import com.example.dakudemo.entity.VisibleRange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Mapper
public interface ApproveMapper {
    /** 添加审批表 */
    public boolean addApprove(Approve approve);

    /** 更新审批表 */
    public boolean updateApprove(Approve approve);

    /** 根据id或者doc_id删除审批表 */
    public boolean deleteApprove(@Param("id")Integer id, @Param("document_id")String document_id);

    /** 查询审批表 */
    public List<Approve> getApprove(@Param("id")Integer id, @Param("document_id")String document_id,
                                    @Param("approve_type")Integer approve_type,@Param("document_type") Integer document_type,
                                    @Param("user_id")Integer user_id,@Param("system_id") Integer system_id,
                                    @Param("approve_person_id")Integer approve_person_id,
                                    @Param("approve_node")Integer approve_node, @Param("approve_status")Integer approve_status);

    /** 查询审批流程中节点对应role*/
    public Integer getRoleIdByApproveNodeAndType(@Param("approve_type")Integer approve_type, @Param("approve_node")Integer approve_node);

    /** 个人查询当前最新审批表 */
    public List<Approve> getOnesCurrentApproves(@Param("user_id") Integer user_id, @Param("document_id") String document_id);

    /** 根据身份role获取type和节点node */
    public List<ApproveProcess> getApproveProcessByRoleId(Integer role_id);

    /** 根据身份role获取可视范围 */
    public VisibleRange getVisibleRangeByRoleId(Integer role_id);

    /** 根据申请人系统和部门以及审批节点获取审批表*/
    public List<Approve> getApprovesBySysAndDepartment(@Param("approve_type")Integer approve_type, @Param("approve_node")Integer approve_node ,
                                                       @Param("system_id") Integer system_id, @Param("department_id") Integer department_id);

    /** 根据审批类型获取审批流程 */
    public List<ApproveProcess> getApproveProcessByApproveType(Integer approve_type);

    /** 根据审批类型获取审批流程最后一个节点 */
    public ApproveProcess getLastApproveProcessByApproveType(Integer approve_type);

    /** 根据审批类型获取审批流程第一个节点 */
    public ApproveProcess getFirstApproveProcessByApproveType(Integer approve_type);

    /** 获取审批人特殊身份的人员 */
    public Integer getVeryImportantPerson(Integer user_id);

    /** 根据user_id和system_id获取角色 */
    public Role getRoleByUserIdAndSysId(@Param("user_id") Integer user_id, @Param("system_id") Integer system_id);

    /** 根据Node和docId获取ApproveId*/
    public Integer getApproveIdByNodeAndDocId(@Param("document_id") String document_id, @Param("approve_node") Integer approve_node);

    /** 根据user_id 获取roles */
    public List<Role> getRolesByUserId(Integer user_id);

    /**根据systemId 获取departmentId*/
    public Integer getDepartmentIdBySysId(Integer system_id);

    /** 根据系统id获取系统名称*/
    public String getSysNameBySysId(Integer id);

    /** 查询文件状态描述 */
    public String getStatusDes(Integer document_status);

    /** 根据部门id获取部门名称 */
    public String getDepartmentNameByDepartmentId(Integer department_id);

    /** 根据系统id，role_id, 部门id获取用户ids */
    public List<Integer> getUserIdsParams(@Param("role_id")Integer role_id, @Param("system_id")Integer system_id, @Param("department_id")Integer department_id);
}
