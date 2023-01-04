package com.example.dakudemo.entity;

import lombok.Data;

/**
 * @author chh
 * @date 2022/3/1 16:57
 */
@Data
public class DeviceStatusRecord {
    private Integer id;
    private String log_id;
    private String device_id;
    private String record_details;
    private String record_time;
    private Integer record_person_id;

    private String record_person_name;
    private Device device;

    public DeviceStatusRecord(Integer id, String log_id, String device_id, String record_details, String record_time, Integer record_person_id) {
        this.id = id;
        this.log_id = log_id;
        this.device_id = device_id;
        this.record_details = record_details;
        this.record_time = record_time;
        this.record_person_id = record_person_id;
    }
    public DeviceStatusRecord() {
    }
}
