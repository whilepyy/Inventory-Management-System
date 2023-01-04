package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import com.example.dakudemo.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chh
 * @date 2022/1/22 18:23
 */
@Service
public class RepairImpl implements RepairService{


    @Autowired
    private RepairMapper repairMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DocumentDeviceMapper documentDeviceMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;
    @Autowired
    private DocumentDeviceService documentDeviceService;

    public Boolean addRepair(Repair repair) {
        return repairMapper.addRepair(repair);
    }

    public Boolean deleteRepair(Integer id) {
        return repairMapper.deleteRepair(id);
    }

    public Boolean updateRepair(Repair repair) {
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(repair.getDocument_id());
        for(DocumentDevice documentDevice:repair.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return repairMapper.updateRepair(repair);
    }

    public List<Repair> getRepairList() {
        List <Repair> repairList = repairMapper.getRepairList();
        repairList = deviceDocumentService.setDevicesAndDocumentDevice(repairList);
        return repairList;
    }
    public List<Repair> getRepairListParams(String document_id,Integer check_repair_category_id, Integer document_status) {
        List <Repair> repairList = repairMapper.getRepairListParams(document_id,check_repair_category_id, document_status);
        repairList = deviceDocumentService.setDevicesAndDocumentDevice(repairList);
        return repairList;
    }

    public List<RepairCategory> getRepairCategoryList(){
        return repairMapper.getRepairCategory();
    }

    public List<RepairCause> getRepairCauseList(){
        return repairMapper.getRepairCause();
    }

    /**根据id查询报检报修表*/
    public Repair getRepairById(Integer id){
        return repairMapper.getRepairById(id);
    }
}
