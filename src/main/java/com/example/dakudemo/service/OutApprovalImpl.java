package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.User;
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
public class OutApprovalImpl implements OutApprovalService {

    @Autowired
    private OutApprovalMapper outApprovalMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;

    /**查询out审批列表信息**/
    public List<Inout> getOutApprovalList(String approve_status,Integer approve_person_id){
        List<Inout> outApprovals = outApprovalMapper.getOutApprovalList(approve_status,approve_person_id);
        outApprovals = deviceDocumentService.setDevicesAndDocumentDevice(outApprovals);
        return outApprovals;
    }
    public List<Inout> getOutApprovalListAll(String approve_status){
        List<Inout> outApprovals = outApprovalMapper.getOutApprovalListAll(approve_status);
        outApprovals = deviceDocumentService.setDevicesAndDocumentDevice(outApprovals);
        return outApprovals;
    }
    /**修改out审批状态**/
    public Boolean updateOutApproval(Integer id,String approve_status,String approve_time){
        return outApprovalMapper.updateOutApproval(id,approve_status,approve_time);
    }

    /**删除出库申请**/
    public Boolean deleteOutApproval(Integer id){
        return outApprovalMapper.deleteOutApproval(id);
    }
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id){
        return outApprovalMapper.getDocumentIdById(id);
    }
}
