package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceStatusRecord;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.mapper.DeviceStatusMapper;
import com.example.dakudemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/2 10:06
 */
@Service
public class DeviceStatusImpl implements DeviceStatusService{
    @Autowired
    private DeviceStatusMapper deviceStatusMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserMapper userMapper;


    /**添加台账记录**/
    public boolean addDeviceStatusRecord(DeviceStatusRecord record){
        return deviceStatusMapper.addDeviceStatusRecord(record);
    }

    /**删除台账记录根据id**/
    public boolean deleteDeviceStatusRecordById(Integer id){
        return deviceStatusMapper.deleteDeviceStatusRecordById(id);
    }

    /**删除台账记录根据log_id**/
    public boolean deleteDeviceStatusRecordByLogId(String log_id){
        return deviceStatusMapper.deleteDeviceStatusRecordByLogId(log_id);
    }

    /**更新台账记录**/
    public boolean updateDeviceStatusRecord(DeviceStatusRecord record){
        return deviceStatusMapper.updateDeviceStatusRecord(record);
    }

    /**查询单条台账记录根据id**/
    public DeviceStatusRecord getDeviceStatusRecordById(Integer id){
        DeviceStatusRecord deviceStatusRecord = deviceStatusMapper.getDeviceStatusRecordById(id);
        Device device = deviceMapper.getDeviceInfoByDeviceId(deviceStatusRecord.getDevice_id());
        deviceStatusRecord.setDevice(device);
        String record_person_name =userMapper.getDisplayNameById(deviceStatusRecord.getRecord_person_id());
        deviceStatusRecord.setRecord_person_name(record_person_name);
        return deviceStatusRecord;
    }

    /**查询多条台账记录**/
    public List<DeviceStatusRecord> getDeviceStatusRecordListParams(String log_id, String device_id, Integer record_person_id){
        List<DeviceStatusRecord> recordList = deviceStatusMapper.getDeviceStatusRecordListParams(log_id, device_id, record_person_id);
        if(CollectionUtils.isEmpty(recordList)){
            return null;
        }
        recordList.forEach(record -> {
            Device device = deviceMapper.getDeviceInfoByDeviceId(record.getDevice_id());
            record.setDevice(device);
            String record_person_name =userMapper.getDisplayNameById(record.getRecord_person_id());
            record.setRecord_person_name(record_person_name);
        });
        return recordList;
    }
    public List<DeviceStatusRecord> getDeviceStatusRecordListParamsAll(){
        List<DeviceStatusRecord> recordList = deviceStatusMapper.getDeviceStatusRecordListParamsAll();
        if(CollectionUtils.isEmpty(recordList)){
            return null;
        }
        recordList.forEach(record -> {
            Device device = deviceMapper.getDeviceInfoByDeviceId(record.getDevice_id());
            record.setDevice(device);
            String record_person_name =userMapper.getDisplayNameById(record.getRecord_person_id());
            record.setRecord_person_name(record_person_name);
        });
        return recordList;
    }

}
