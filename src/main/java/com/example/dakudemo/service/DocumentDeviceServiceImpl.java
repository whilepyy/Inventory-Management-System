package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:10
 */
@Service
public class DocumentDeviceServiceImpl implements DocumentDeviceService {
    @Autowired
    private DocumentDeviceMapper documentDeviceMapper;

    /**通过document_id 获取到设备相关信息，包括device_id和device_number**/
    public List<DocumentDevice> getDocumentDeviceInfo(String document_id){
        return documentDeviceMapper.getDocumentDeviceInfo(document_id);
    }

    /**添加documentDevice**/
    public Boolean addDocumentDevice(DocumentDevice documentDevice){
        return documentDeviceMapper.addDocumentDevice(documentDevice);
    }
    public Boolean updateDocumentDevice(DocumentDevice documentDevice){
        return documentDeviceMapper.updateDocumentDevice(documentDevice);
    }
    public Boolean updateDocumentDeviceNew(DocumentDevice documentDevice,Integer documentDeviceId){
        return documentDeviceMapper.updateDocumentDeviceNew(documentDevice,documentDeviceId);
    }
    /*chh add*/
    /**通过Id删除documentDevice**/
    public Boolean deleteDocumentDeviceById(Integer id){
        return documentDeviceMapper.deleteDocumentDeviceById(id);
    }

    /**通过documentId获取deviceIds**/
    public Boolean deleteDocumentDeviceByDocId(String document_id){
        return documentDeviceMapper.deleteDocumentDeviceByDocId(document_id);
    }

    /**通过documentId和DeviceId获取documentDevice**/
    public DocumentDevice getDocumentDeviceByDocIdAndDeviceId(String document_id, String device_id){
        return documentDeviceMapper.getDocumentDeviceByDocIdAndDeviceId(document_id,device_id);
    }

    /**通过documentId获取deviceIds**/
    public List<DocumentDevice> getDeviceIdsByDocumentId(String document_id){
        return documentDeviceMapper.getDeviceIdsByDocumentId(document_id);
    }
}
