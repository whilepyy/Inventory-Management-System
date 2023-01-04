package com.example.dakudemo.service;

import com.example.dakudemo.entity.DeviceDocument;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 15:15
 * 为所有包含document_id的对象添加设备与DocumentDevice
 */
public interface DeviceDocumentService {

    /**为列表对象设置设备列表和DocumentDevice列表**/
    public <T extends DeviceDocument> List<T> setDevicesAndDocumentDevice(List<T> tList);

    /**为单个对象设置设备列表和DocumentDevice列表**/
    public <T extends DeviceDocument> T setSingleDevicesAndDocumentDevice(T t);

    /** 重载上述方法： flag = 1 从临时仓库内查询设备; flag = 0 从真实仓库内查询设备**/
    public <T extends DeviceDocument> List<T> setDevicesAndDocumentDevice(List<T> tList, Integer flag);

    /** 同上方法 **/
    public <T extends DeviceDocument> T setSingleDevicesAndDocumentDevice(T t, Integer flag);

}
