package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Repair;
import com.example.dakudemo.entity.Return;
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
public class ReturnApprovalImpl implements ReturnApprovalService{

    @Autowired
    private ReturnApprovalMapper returnApprovalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DocumentDeviceMapper documentDeviceMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;

    /**查询归还单审批列表信息**/
    public List<Return> getReturnApprovalList(String approve_status, Integer approve_person_id){
        List<Return> returnApprovals = returnApprovalMapper.getReturnApprovalList(approve_status,approve_person_id);
        returnApprovals = deviceDocumentService.setDevicesAndDocumentDevice(returnApprovals);
//        for (Return returnApproval : returnApprovals) {
//            //String userName = userMapper.(returnApproval.getDocument_person_id());
//            //returnApproval.setDocument_person_name(userName);
//            //List<DocumentDevice> documentDevices = documentDeviceMapper.getDeviceIdsByDocumentId(returnApproval.getDocument_id());
//            //returnApproval.setDocumentDeviceList(documentDevices);
//            //List<Device> deviceList = new ArrayList<>();
//            //for(DocumentDevice documentDevice:documentDevices){
//                //if(documentDevice != null){
//                    //deviceList.add(deviceMapper.getDeviceInfoByDeviceId(documentDevice.getDevice_id()));
//                //}
//            //}
//            //returnApproval.setDevice(deviceList);
//        }
        return returnApprovals;
    }
    public List<Return> getReturnApprovalListAll(String approve_status){
        List<Return> returnApprovals = returnApprovalMapper.getReturnApprovalListAll(approve_status);
        returnApprovals = deviceDocumentService.setDevicesAndDocumentDevice(returnApprovals);
        return returnApprovals;
    }
    /**修改归还单审批状态**/
    public Boolean updateReturnApproval(Integer id,String approve_status,String approve_time){
        return returnApprovalMapper.updateReturnApproval(id,approve_status,approve_time);
    }

    /**删除归还单申请**/
    public Boolean deleteReturnApproval(Integer id){
        return returnApprovalMapper.deleteReturnApproval(id);
    }
    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id){
        return returnApprovalMapper.getDocumentIdById(id);
    }
}
