package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import org.apache.ibatis.annotations.Param;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;


public interface ApproveService {
    /**
     * 添加审批表
     */
    public boolean addApprove(Approve approve);

    /**
     * 更新审批表
     */
    public boolean updateApprove(Approve approve);

    /**
     * 根据id或者doc_id删除审批表
     */
    public boolean deleteApprove(Integer id, String document_id);

    /**
     * 查询审批表
     */
    public List<Approve> getApprove(Integer id, String document_id,
                                    Integer approve_type,Integer document_type,
                                    Integer user_id,Integer system_id,
                                    Integer approve_person_id,
                                    Integer approve_node, Integer approve_status);

    /**
     * 根据前端传入的类型与设备列表
     */
    public Integer whatRealApproveType(Integer inputType, List<DocumentDevice> documentDeviceList);

    /**
     * 生成下一个级别的审批表
     */
    public boolean addNextLevelApprove(String document_id, Integer user_id, Integer system_id,Integer approve_type, Integer document_type, Integer next_node);

    /**
     * 个人查询自己提交的审批表
     */
    public List<Approve> getOnesPostApproves(Integer user_id, String document_id);

    /**
     * 个人查询自己提交的当前最新审批表
     */
    public List<Approve> getOnesPostCurrentApproves(Integer user_id, String document_id);

    /**
     * 个人查询自己提交的未审批表
     */
    public List<Approve> getOnesPostPendingApproves(Integer user_id, String document_id);

    /**
     * 审批人查询到达自己审批流程的审批表
     */
    public List<Approve> getApproverCurrentApproves(Integer approver_id);

    /**
     * 审批人查询自己审批过的审批表
     */
    public List<Approve> getApproverProcessedApproves(Integer approver_id);

    /**
     * 审批人更新审批表(审批)
     */
    public boolean approveIt(Approve approve);

    /**
     * 判断申请人是否在审批人的可视范围 -1 代表不在范围
     */
    public Integer isUserInVisible(Integer user_id, Integer approver_id, Integer system_id);

    /**
     * 判断审批人是否为特殊身份人员
     */
    public boolean isVeryImportantPerson(Integer approver_id);

    /**
     * 审批后更新库存
     */
    public boolean updateStock(String document_id);

    /**
     * 更新库存，手动指定类型
     * */
    public boolean updateStockNoAuto(String document_id, Integer docType);

    /**
     * 回退数据库更新，自动判断类型
     * */
    public boolean fallback(String document_id);

    /**
     * 回退数据库更新，手动指定类型
     * */
    public boolean fallbackNoAuto(String document_id, Integer docType);


    /** 根据审批类型获取审批流程第一个节点 */
    public ApproveProcess getFirstApproveProcessByApproveType(Integer approve_type);

    /** 根据用户id和审批类型获取下一个节点*/
    public ApproveProcess getNextApproveProcessByUserIdAndApproveType(Integer user_id, Integer approve_type, Integer system_id);

    /** 审批人根据document_id和审批人信息查询审批链条 */
    public List<Approve> getApproveChainByDocIdAndApproverId(String document_id, Integer approver_id);

    /** 根据审批人以及审批类型获得审批接节点 */
    public ApproveProcess getApproveProcessByApproverIdAndApproveType(Integer approver_id, Integer approve_type, Integer system_id);

    /** 根据user_id和system_id获取角色 */
    public Role getRoleByUserIdAndSysId(Integer user_id, Integer system_id);

    /** 为审批表添加人员信息 */
    public List<Approve> addInfoForApproves(List<Approve> approveList);

    /** 为审批表添加单据信息 */
    public List<Approve> addDocInfoForApproves(List<Approve> approveList);

    /** 根据user_id 获取roles */
    public List<Role> getRolesByUserId(Integer user_id);

    /** 查询文件状态描述 */
    public String getStatusDes(Integer document_status);

    /** 跟新单据状态 无限制*/
    public boolean updateDocStatus(String document_id, Integer docStatus);

    /** 查询单据的系统部门信息*/
    public List<Role> getSysDepartInfoByDocId(String document_id, Integer doc_status, Integer docType, String displayName);

    // 判断是否还有库存的核心方法
    public List<DocumentDevice> getAllOnApprovingDevices(List<DocumentDevice> onApprovingDevicesList, Integer docType);

    // 判断库存是否充足
    public Result isEnough(Integer docType, Inout inout, Lend lend);


    public Result isEnough(Integer docType, DeviceDocument deviceDocument);

    // 判断借单是否超出
    public Result isBeyondForLendDoc(Integer docType,  DeviceDocument doc, String lend_document_id);

    /** 查询审批人 **/
    public Result getApproversByApprove(Approve approve);

    // 判断是否还有库存的核心方法
    public List<DocumentDevice> getAllOnApprovingDevicesForLendDoc(List<DocumentDevice> onApprovingDevicesList, Integer docType, String lend_document_id);

//    /** 库管员确认单据 */
//    public boolean confirmApprove(Approve approve);
}