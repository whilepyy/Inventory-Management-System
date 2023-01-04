package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.DeviceStatusRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 17:06
 */
@Repository
@Mapper
public interface DeviceStatusMapper {

    /**添加台账记录**/
    public boolean addDeviceStatusRecord(DeviceStatusRecord record);

    /**删除台账记录根据id**/
    public boolean deleteDeviceStatusRecordById(Integer id);

    /**删除台账记录根据log_id**/
    public boolean deleteDeviceStatusRecordByLogId(String log_id);

    /**更新台账记录**/
    public boolean updateDeviceStatusRecord(DeviceStatusRecord record);

    /**查询单条台账记录根据id**/
    public DeviceStatusRecord getDeviceStatusRecordById(Integer id);

    /**查询多条台账记录**/
    public List<DeviceStatusRecord> getDeviceStatusRecordListParams(@Param("log_id")String log_id, @Param("device_id")String device_id,
                                                                    @Param("record_person_id")Integer record_person_id);
    public List<DeviceStatusRecord> getDeviceStatusRecordListParamsAll();
}
