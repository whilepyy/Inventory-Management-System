package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.DocumentDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 11:05
 */
@Repository
@Mapper
public interface DocumentDeviceMapper {


    /**通过document_id 获取到设备相关信息，包括device_id和device_number**/
    @Results(
            value = {
                    @Result(id=true,property="id",column = "id"),
                    @Result(property="device_id",column = "device_id"),
                    @Result(property="device_number",column = "device_number"),
                    @Result(property="device_status",column = "device_status")
            }
    )
    public List<DocumentDevice> getDocumentDeviceInfo(String document_id);

    /**添加documentDevice**/
    public Boolean addDocumentDevice(DocumentDevice documentDevice);
    /**update documentDevice**/
    public Boolean updateDocumentDevice(DocumentDevice document);
    public Boolean updateDocumentDeviceNew(@Param("documentDevice") DocumentDevice documentDevice,@Param("documentDeviceId") Integer documentDeviceId);
    /**通过documentId获取deviceIds**/
    public List<DocumentDevice> getDeviceIdsByDocumentId(String document_id);

    /*chh add*/
    /**通过Id删除documentDevice**/
    public Boolean deleteDocumentDeviceById(@Param("id") Integer id);

    /**通过documentId删除documentDevice**/
    public Boolean deleteDocumentDeviceByDocId(@Param("document_id") String document_id);

    /**通过documentId和DeviceId获取documentDevice**/
    public DocumentDevice getDocumentDeviceByDocIdAndDeviceId(@Param("document_id") String document_id, @Param("device_id") String device_id);

}
