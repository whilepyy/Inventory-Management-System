package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.FundCategory;
import com.example.dakudemo.mapper.DeviceMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:10
 */
@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;
    /**带参查询device列表信息**/
    public List<Device> getDeviceListParams(String device_id,String name,Integer category_id,String location){
        return deviceMapper.getDeviceListParams(device_id,name,category_id,location);
    }
    /**带参查询device列表信息**/
    public List<Device> getDeviceList(){
        return deviceMapper.getDeviceList();
    }
    /**通过设备id获取设备信息**/
    public Device getDeviceInfoByDeviceId(String deviceId){
        return deviceMapper.getDeviceInfoByDeviceId(deviceId);
    }
    /**新增device**/
    public Boolean addDevice(Device device){
        return deviceMapper.addDevice(device);
    }
    /**修改device**/
    public Boolean updateDevice(Device device){
        return deviceMapper.updateDevice(device);
    }
    public Boolean updateDeviceNew(Device device,Integer deviceId){
        return deviceMapper.updateDeviceNew(device,deviceId);
    }
    /**通过设备类别id获取设备类别名称**/
    public String getDeviceCategoryNameById(int id){
        return deviceMapper.getDeviceCategoryNameById(id);
    }
    /** 通过设备类别缩写获取设备类别名称 **/
    public String getDeviceCategoryNameByNu(String category_nu){
        return deviceMapper.getDeviceCategoryNameByNu(category_nu);
    }
    /**获取设备类别**/
    public List<DeviceCategory> getDeviceCategory(){
        return deviceMapper.getDeviceCategory();
    }
    public List<FundCategory> getFundCategory(){
        return deviceMapper.getFundCategory();
    }
    /***修改库存数量*/
    public Boolean updateInventoryNumber(String device_id,Integer addNumber){
        return deviceMapper.updateInventoryNumber(device_id,addNumber);
    }
    /***库存数量-*/
    public Boolean updateMinusInventoryNumber(String device_id,Integer minusNumber){
        return deviceMapper.updateMinusInventoryNumber(device_id,minusNumber);
    }
    /***出库数量+ */
    public Boolean updateAddOutwarehouseNumber(String device_id,Integer addNumber){
        return deviceMapper.updateAddOutwarehouseNumber(device_id,addNumber);
    }
    /***减少出库数量 */
    public Boolean updateMinusOutwarehouseNumber(String device_id, Integer minusNumber){
        return deviceMapper.updateMinusOutwarehouseNumber(device_id, minusNumber);
    }
    /***获取已入库的所有设备编号*/
    public List<String> getAllDeviceIds(){
        return deviceMapper.getAllDeviceIds();
    }

    /***减小借出数量*/
    public Boolean updateMinusLendNumber(String device_id, Integer minusNumber){
        return deviceMapper.updateMinusLendNumber(device_id,minusNumber);
    }

    /***增加借出数量+ */
    public Boolean updateAddLendNumber(String device_id, Integer addNumber){
        return deviceMapper.updateAddLendNumber(device_id,addNumber);
    }

    /***减小归还数量*/
    public Boolean updateMinusReturnNumber(String device_id, Integer minusNumber){
        return deviceMapper.updateMinusReturnNumber(device_id, minusNumber);
    }

    /***增加归还数量+ */
    public Boolean updateAddReturnNumber(String device_id, Integer addNumber){
        return deviceMapper.updateAddReturnNumber(device_id,addNumber);
    }
    /*增加报检报修数量*/
    public Boolean updateAddRepairwarehouseNumber(String device_id,Integer addNumber){
        return deviceMapper.updateAddRepairwarehouseNumber(device_id,addNumber);
    }
    /*减少报检报修数量*/
    public Boolean updateMinusRepairwarehouseNumber(String device_id,Integer minusNumber){
        return deviceMapper.updateMinusRepairwarehouseNumber(device_id,minusNumber);
    }
	
	/***减小报废数量*/
    public Boolean updateMinusScrapNumber(String device_id, Integer minusNumber){
        return deviceMapper.updateMinusScrapNumber(device_id, minusNumber);
    }

    /***增加报废数量+ */
    public Boolean updateAddScrapNumber(String device_id, Integer addNumber){
        return deviceMapper.updateAddScrapNumber(device_id, addNumber);
    }
    /*增加报检报修返回数量*/
    public Boolean updateAddRestockWarehouseNumber(String device_id,Integer addNumber){
        return deviceMapper.updateAddRestockWarehouseNumber(device_id, addNumber);
    }

    /*减少报检报修返回数量*/
    public Boolean updateMinusRestockWarehouseNumber(String device_id,Integer minusNumber){
        return deviceMapper.updateMinusRestockWarehouseNumber(device_id, minusNumber);
    }

    /** 添加一个临时设备 */
    public Boolean addDeviceTemp(Device device){
        return deviceMapper.addDeviceTemp(device);
    }

    /** 删除一个临时设备 */
    public Boolean deleteDeviceTemp(Integer id, String device_id){
        if (id == null && device_id == null){
            return false;
        }
        return deviceMapper.deleteDeviceTemp(id, device_id);
    }

    /** 更新一个临时设备 */
    public Boolean updateDeviceTemp(Device device){
        return deviceMapper.updateDeviceTemp(device);
    }

    /** 查询临时设备 */
    public List<Device> getDeviceTempList( Integer id, String device_id,
                                           String name, Integer category_id,
                                           String location, String document_id){
        return deviceMapper.getDeviceTempList(id, device_id, name, category_id, location, document_id);
    }

    /** 将临时仓库内的设备转移至真实仓库 */
    public Boolean translateDeviceTempToDevice(String device_id, String document_id){
        List<Device> deviceTempList = deviceMapper.getDeviceTempList(null, device_id,
                null, null, null, document_id);
        if(ObjectUtils.isEmpty(deviceTempList)){
            return false;
        }
        Device device = deviceTempList.get(0);
        List<Device> deviceList = deviceMapper.getDeviceListParams(device_id, null, null, null);
        Boolean isSuccess = true;
        if(ObjectUtils.isEmpty(deviceList)){
            // 如果真实仓库没有这个设备记录，则完全拷贝一份
            isSuccess = deviceMapper.translateDeviceTempToDevice(device_id, document_id);
        }
        return isSuccess;
    }
}
