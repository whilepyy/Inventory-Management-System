package com.example.dakudemo.service;


import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import com.example.dakudemo.mapper.LendApprovalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chh
 * @date 2022/1/25 12:12
 */

@Service
public class LendApprovalImpl implements LendApprovalService {

    @Autowired
    private LendApprovalMapper lendApprovalMapper;

    @Autowired
    private DeviceDocumentService deviceDocumentService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    DocumentDeviceMapper documentDeviceMapper;

    /**查询Lend列表信息**/
    public List<Lend> getLendApprovalList(String approve_status, Integer approve_person_id){
        List<Lend> lendList =  lendApprovalMapper.getLendApprovalList(approve_status, approve_person_id);
        //  为lendList的每一个lend添加设备
//        lendList.forEach(lend -> {
//
//            List<DocumentDevice> documentDeviceList = documentDeviceMapper.getDeviceIdsByDocumentId(lend.getDocument_id());
//            lend.setDocumentDeviceList(documentDeviceList);
//            List<Device> newDeviceList = new ArrayList<>();
//            documentDeviceList.forEach(documentDevice -> {
//
//                List<Device> deviceList = deviceService.getDeviceListParams(documentDevice.getDevice_id(),null,null,null);
//                deviceList.forEach(newDeviceList::add);
//            });
//            lend.setDevices(newDeviceList);
//        });
        //
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }
    public List<Lend> getLendApprovalListAll(String approve_status){
        List<Lend> lendList =  lendApprovalMapper.getLendApprovalListAll(approve_status);
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }
    /**修改lend**/
    public Boolean updateLendApproval(Integer id,String approve_status,String approve_time){
        return lendApprovalMapper.updateLendApproval(id, approve_status, approve_time);
    }

    /**删除出库申请**/
    public Boolean deleteLendApproval(Integer id){
        return lendApprovalMapper.deleteLendApproval(id);
    }

    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id){
        return lendApprovalMapper.getDocumentIdById(id);
    }
}
