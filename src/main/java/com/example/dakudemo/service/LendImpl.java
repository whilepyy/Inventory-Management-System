package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import com.example.dakudemo.mapper.LendMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chh
 * @date 2022/1/22 18:23
 */
@Service
public class LendImpl implements LendService{

    @Autowired
    private LendMapper lendMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;
    @Autowired
    private DocumentDeviceService documentDeviceService;

    public Boolean addLend(Lend lend) {
        return lendMapper.addLend(lend);
    }

    public Boolean deleteLend(Integer id) {
        return lendMapper.deleteLend(id);
    }

    public Boolean updateLend(Lend lend) {
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(lend.getDocument_id());
        for(DocumentDevice documentDevice:lend.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return isSuccess && lendMapper.updateLend(lend);
    }

    public List<Lend> getLendListByDocId(String document_id) {
        List<Lend> lendList = lendMapper.getLendListByDocId(document_id);
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }

    public List<Lend> getLendListByUserId(String device_user_id) {
        List<Lend> lendList = lendMapper.getLendListByUserId(device_user_id);
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }

    /**根据id查询借出*/
    public Lend getLendById(Integer id){
        Lend lend = lendMapper.getLendById(id);
        deviceDocumentService.setSingleDevicesAndDocumentDevice(lend);
        return lend;
    }

    /**根据id查询借出document_id*/
    public String getLendDocIdById(Integer id){
        return lendMapper.getLendDocIdById(id);
    }

    public List<Lend> getLendList(String document_id,Integer device_user_id, Integer document_status) {
        List<Lend> lendList =  lendMapper.getLendList(document_id,device_user_id, document_status);
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }
    public List<Lend> getLendListAll(){
        List<Lend> lendList =  lendMapper.getLendListAll();
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }

    public List<Lend> getLendListById(String document_id){
        List<Lend> lendList =  lendMapper.getLendListById(document_id);
        lendList = deviceDocumentService.setDevicesAndDocumentDevice(lendList);
        return lendList;
    }
}
