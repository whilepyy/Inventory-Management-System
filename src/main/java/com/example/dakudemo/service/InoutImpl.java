package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import com.example.dakudemo.mapper.InoutMapper;
import com.example.dakudemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:10
 */
@Service
public class InoutImpl implements InoutService {

    @Autowired
    private InoutMapper inoutMapper;
    @Autowired
    private DeviceDocumentService deviceDocumentService;
    @Autowired
    private DocumentDeviceService documentDeviceService;

    /**带参查询in列表信息**/
    public List<Inout> getInListParams(String document_id,Integer document_category_id, Integer document_status, Integer buy_use_person_id){
        List<Inout> inList = inoutMapper.getInListParams(document_id,document_category_id, document_status, buy_use_person_id);
        inList = deviceDocumentService.setDevicesAndDocumentDevice(inList, 1);
        return inList;
    }
    /**查询in列表信息**/
    public List<Inout> getInList(Integer document_category_id){
        List<Inout> inList = inoutMapper.getInList(document_category_id);
        inList = deviceDocumentService.setDevicesAndDocumentDevice(inList, 1);
        return inList;
    }
    /**新增in**/
    public Boolean addIn(Inout inout){
        return inoutMapper.addIn(inout);
    }
    /**修改in**/
    public Boolean updateIn(Inout inout){
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(inout.getDocument_id());
        for(DocumentDevice documentDevice:inout.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return isSuccess &&inoutMapper.updateIn(inout);
    }
    public Boolean updateInNew(Inout inout,Integer id){
        return inoutMapper.updateInNew(inout,id);
    }
    /**删除in**/
    public Boolean deleteIn(Integer id){
        return inoutMapper.deleteIn(id);
    }

    /**带参查询out列表信息**/
    public List<Inout> getOutListParams(String document_id,Integer document_category_id,Integer buy_use_person_id, Integer document_status){
        List<Inout> outList = inoutMapper.getOutListParams(document_id,document_category_id,buy_use_person_id, document_status);
        outList = deviceDocumentService.setDevicesAndDocumentDevice(outList);
        return outList;
    }
    /**查询out列表信息**/
    public List<Inout> getOutList(Integer document_category_id){
        List<Inout> outList = inoutMapper.getOutList(document_category_id);
        outList = deviceDocumentService.setDevicesAndDocumentDevice(outList);
        return outList;
    }
    /**新增out**/
    public Boolean addOut(Inout inout){
        return inoutMapper.addOut(inout);
    }
    /**修改out**/
    public Boolean updateOut(Inout inout){
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(inout.getDocument_id());
        for(DocumentDevice documentDevice:inout.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return isSuccess && inoutMapper.updateOut(inout);
    }
    /**删除out**/
    public Boolean deleteOut(Integer id){
        return inoutMapper.deleteOut(id);
    }

    /** 根据id查询out */
    public Inout getInoutById(Integer id){
        return inoutMapper.getInoutById(id);
    }
}
