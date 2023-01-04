package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceDocument;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DocumentDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 15:20
 */
@Service
public class DeviceDocumentServiceImpl implements  DeviceDocumentService{

    @Autowired
    private DocumentDeviceMapper documentDeviceMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserService userService;

    /**设置设备列表和DocumentDevice列表**/
    public <T extends DeviceDocument> List<T> setDevicesAndDocumentDevice(List<T> tList){
        if (CollectionUtils.isEmpty(tList)){
            return tList;
        }
        tList.forEach(t -> {
            String approvalName = userService.getDisplayNameById(t.getApprove_person_id());
            String username = userService.getDisplayNameById(t.getUser_id());
            t.setUsername(username);
            t.setApproveName(approvalName);
            List<DocumentDevice> documentDeviceList = documentDeviceMapper.getDeviceIdsByDocumentId(t.getDocument_id());
            t.setDocumentDeviceList(documentDeviceList);
            List<Device> newDeviceList = new ArrayList<>();
            documentDeviceList.forEach(documentDevice -> {
                List<Device> deviceList = deviceMapper.getDeviceListParams(documentDevice.getDevice_id(),null,null,null);
                newDeviceList.addAll(deviceList);
            });
            t.setDevices(newDeviceList);
        });
        return tList;
    }

    /**为单个对象设置设备列表和DocumentDevice列表**/
    public <T extends DeviceDocument> T setSingleDevicesAndDocumentDevice(T t){
        if(ObjectUtils.isEmpty(t)){
            return t;
        }
        String approvalName = userService.getDisplayNameById(t.getApprove_person_id());
        String username = userService.getDisplayNameById(t.getUser_id());
        t.setUsername(username);
        t.setApproveName(approvalName);
        List<DocumentDevice> documentDeviceList = documentDeviceMapper.getDeviceIdsByDocumentId(t.getDocument_id());
        t.setDocumentDeviceList(documentDeviceList);
        List<Device> newDeviceList = new ArrayList<>();
        documentDeviceList.forEach(documentDevice -> {
            List<Device> deviceList = deviceMapper.getDeviceListParams(documentDevice.getDevice_id(),null,null,null);
            newDeviceList.addAll(deviceList);
        });
        t.setDevices(newDeviceList);
        return t;
    }

    /** 重载上述方法： flag = 1 从临时仓库内查询设备; flag = 0 从真实仓库内查询设备**/
    public <T extends DeviceDocument> List<T> setDevicesAndDocumentDevice(List<T> tList, Integer flag){
        if(flag.equals(0)){
            return setDevicesAndDocumentDevice(tList);
        }
        if (CollectionUtils.isEmpty(tList)){
            return tList;
        }
        tList.forEach(t -> {
            String approvalName = userService.getDisplayNameById(t.getApprove_person_id());
            String username = userService.getDisplayNameById(t.getUser_id());
            t.setUsername(username);
            t.setApproveName(approvalName);
            List<DocumentDevice> documentDeviceList = documentDeviceMapper.getDeviceIdsByDocumentId(t.getDocument_id());
            t.setDocumentDeviceList(documentDeviceList);
            List<Device> newDeviceList = new ArrayList<>();
            documentDeviceList.forEach(documentDevice -> {
                List<Device> deviceList = deviceMapper.getDeviceTempList(null, documentDevice.getDevice_id(),
                        null,null,null, t.getDocument_id());
                newDeviceList.addAll(deviceList);
            });
            t.setDevices(newDeviceList);
        });
        return tList;

    }

    /** 同上方法 **/
    public <T extends DeviceDocument> T setSingleDevicesAndDocumentDevice(T t, Integer flag){
        if(flag.equals(0)){
            return setSingleDevicesAndDocumentDevice(t);
        }
        if(ObjectUtils.isEmpty(t)){
            return t;
        }
        String approvalName = userService.getDisplayNameById(t.getApprove_person_id());
        String username = userService.getDisplayNameById(t.getUser_id());
        t.setUsername(username);
        t.setApproveName(approvalName);
        List<DocumentDevice> documentDeviceList = documentDeviceMapper.getDeviceIdsByDocumentId(t.getDocument_id());
        t.setDocumentDeviceList(documentDeviceList);
        List<Device> newDeviceList = new ArrayList<>();
        documentDeviceList.forEach(documentDevice -> {
            List<Device> deviceList = deviceMapper.getDeviceTempList(null, documentDevice.getDevice_id(),null,null,null, t.getDocument_id());
            newDeviceList.addAll(deviceList);
        });
        t.setDevices(newDeviceList);
        return t;
    }

}
