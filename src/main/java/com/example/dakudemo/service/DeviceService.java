package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.FundCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface DeviceService {
    /**带参查询device列表信息**/
    List<Device> getDeviceListParams(String device_id,String name,Integer category_id,String location);
    /**通过设备id获取设备信息**/
    public Device  getDeviceInfoByDeviceId(String deviceId);
    /**查询device列表信息**/
    List<Device> getDeviceList();
    /**新增device**/
    public Boolean addDevice(Device device);
    /**修改device**/
    public Boolean updateDevice(Device device);
    public Boolean updateDeviceNew(Device device,Integer deviceId);
    /**通过设备类别id获取设备类别名称**/
    public String getDeviceCategoryNameById(int id);

    /** 通过设备类别缩写获取设备类别名称 **/
    public String getDeviceCategoryNameByNu(String category_nu);
    /**获取设备类别**/
    List<DeviceCategory> getDeviceCategory();
    /**获取经费来源**/
    List<FundCategory> getFundCategory();
    /***修改库存数量*/
    public Boolean updateInventoryNumber(String device_id,Integer addNumber);
    /***库存数量-*/
    public Boolean updateMinusInventoryNumber(String device_id,Integer minusNumber);

    /***出库数量+ */
    public Boolean updateAddOutwarehouseNumber(String device_id,Integer addNumber);
    /***减少出库数量 */
    public Boolean updateMinusOutwarehouseNumber(String device_id, Integer minusNumber);
    /***获取已入库的所有设备编号*/
    public List<String> getAllDeviceIds();

    /*chh add*/
    /***减小借出数量*/
    public Boolean updateMinusLendNumber(String device_id, Integer minusNumber);

    /***增加借出数量+ */
    public Boolean updateAddLendNumber(String device_id, Integer addNumber);

    /***减小归还数量*/
    public Boolean updateMinusReturnNumber(String device_id, Integer minusNumber);

    /***增加归还数量+ */
    public Boolean updateAddReturnNumber(String device_id, Integer addNumber);

    /*增加报检报修数量*/
    public Boolean updateAddRepairwarehouseNumber(String device_id,Integer addNumber);
    /*减少报检报修数量*/
    public Boolean updateMinusRepairwarehouseNumber(String device_id,Integer minusNumber);

    /*增加报检报修返回数量*/
    public Boolean updateAddRestockWarehouseNumber(String device_id,Integer addNumber);
    /*减少报检报修返回数量*/
    public Boolean updateMinusRestockWarehouseNumber(String device_id,Integer minusNumber);

	 /***减小报废数量*/
    public Boolean updateMinusScrapNumber(String device_id, Integer minusNumber);

    /***增加报废数量+ */
    public Boolean updateAddScrapNumber(String device_id, Integer addNumber);

    /** 添加一个临时设备 */
    public Boolean addDeviceTemp(Device device);

    /** 删除一个临时设备 */
    public Boolean deleteDeviceTemp(Integer id, String device_id);

    /** 更新一个临时设备 */
    public Boolean updateDeviceTemp(Device device);

    /** 查询临时设备 */
    public List<Device> getDeviceTempList( Integer id, String device_id,
                                           String name, Integer category_id,
                                           String location, String document_id);

    /** 将临时仓库内的设备转移至真实仓库 */
    public Boolean translateDeviceTempToDevice(String device_id, String document_id);

}
