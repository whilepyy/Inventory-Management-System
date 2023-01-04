package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 12:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDocument {
    private String document_id;
    private Integer user_id;
    private String username;
    private Integer approve_person_id;
    private String approveName;
    private List<DocumentDevice> documentDeviceList;
    private List<Device> devices;
}
