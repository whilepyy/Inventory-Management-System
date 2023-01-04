package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.DocumentDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface DocumentDeviceService {
    /**通过document_id 获取到设备相关信息，包括device_id和device_number**/
    public List<DocumentDevice> getDocumentDeviceInfo(String document_id);

    /**添加documentDevice**/
    public Boolean addDocumentDevice(DocumentDevice documentDevice);
    public Boolean updateDocumentDevice(DocumentDevice documentDevice);
    public Boolean updateDocumentDeviceNew(DocumentDevice documentDevice,Integer documentDeviceId);
    /*chh add*/
    /**通过Id删除documentDevice**/
    public Boolean deleteDocumentDeviceById(Integer id);

    /**通过documentId获取deviceIds**/
    public Boolean deleteDocumentDeviceByDocId(String document_id);

    /**通过documentId和DeviceId获取documentDevice**/
    public DocumentDevice getDocumentDeviceByDocIdAndDeviceId(String document_id, String device_id);

    /**通过documentId获取deviceIds**/
    public List<DocumentDevice> getDeviceIdsByDocumentId(String document_id);
}
