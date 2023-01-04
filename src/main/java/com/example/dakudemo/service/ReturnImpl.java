package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Return;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import com.example.dakudemo.mapper.ReturnMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chh
 * @date 2022/1/23 15:03
 */
@Service
public class ReturnImpl implements ReturnService{

    @Autowired
    private ReturnMapper returnMapper;

    @Autowired
    private DeviceDocumentService deviceDocumentService;

    @Autowired
    private DocumentDeviceService documentDeviceService;

    public Boolean addReturn(Return ret) {
        return returnMapper.addReturn(ret);
    }

    public Boolean deleteReturn(Integer id) {
        return returnMapper.deleteReturn(id);
    }

    public Boolean updateReturn(Return ret) {
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(ret.getDocument_id());
        for(DocumentDevice documentDevice:ret.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return isSuccess && returnMapper.updateReturn(ret);
    }

    public List<Return> getReturnListByDocId(String document_id) {
        List<Return> returnList = returnMapper.getReturnListByDocId(document_id);
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
        return returnList;
    }

    public List<Return> getReturnListByUserId(String return_person_id) {
        List<Return> returnList = returnMapper.getReturnListByUserId(return_person_id);
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
        return returnList;
    }

    public Return getReturnById(Integer id){
        Return r = returnMapper.getReturnById(id);
        r = deviceDocumentService.setSingleDevicesAndDocumentDevice(r);
        return r;
    }

    public String getReturnDocIdById(Integer id){
        return returnMapper.getReturnDocIdById(id);
    }

    public List<Return> getReturnList(String document_id,Integer return_person_id, String return_person_name) {
        List<Return> returnList = returnMapper.getReturnList(document_id,return_person_id, return_person_name);
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
        return returnList;
    }
    public List<Return> getReturnListAll(){
        List<Return> returnList = returnMapper.getReturnListAll();
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
        return returnList;
    }
    public List<Return> getReturnListById(String document_id){
        List<Return> returnList = returnMapper.getReturnListById(document_id);
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
        return returnList;
    }
}
