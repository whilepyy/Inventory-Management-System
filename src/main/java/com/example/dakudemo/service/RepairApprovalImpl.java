package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Repair;
import com.example.dakudemo.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:10
 */
@Service
public class RepairApprovalImpl implements RepairApprovalService {

    @Autowired
    private RepairApprovalMapper repairApprovalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DocumentDeviceMapper documentDeviceMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;

    /**查询报检报修审批列表信息**/
    public List<Repair> getRepairApprovalList(String approve_status, Integer approve_person_id){
        List<Repair> repairApprovals = repairApprovalMapper.getRepairApprovalList(approve_status,approve_person_id);
        repairApprovals = deviceDocumentService.setDevicesAndDocumentDevice(repairApprovals);
        return repairApprovals;
    }
    public List<Repair> getRepairApprovalListAll(String approve_status){
        List<Repair> repairApprovals = repairApprovalMapper.getRepairApprovalListAll(approve_status);
        repairApprovals = deviceDocumentService.setDevicesAndDocumentDevice(repairApprovals);
        return repairApprovals;
    }
    /**修改报检报修审批状态**/
    public Boolean updateRepairApproval(Integer id,String approve_status,String approve_time){
        return repairApprovalMapper.updateRepairApproval(id,approve_status,approve_time);
    }

    /**删除报检报修申请**/
    public Boolean deleteRepairApproval(Integer id){
        return repairApprovalMapper.deleteRepairApproval(id);
    }
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id){
        return repairApprovalMapper.getDocumentIdById(id);
    }
}
