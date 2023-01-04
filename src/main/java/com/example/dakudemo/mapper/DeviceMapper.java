package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.FundCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 11:05
 */
@Repository
@Mapper
public interface DeviceMapper {
    /**添加device*/
    public Boolean addDevice(Device device);

    /*** 修改device*/
    public Boolean updateDevice(Device device);
    public Boolean updateDeviceNew(@Param("device") Device device,@Param("deviceId") Integer deviceId);
    public Device getDeviceInfoByDeviceId(String deviceId);

    /**带参查询device列表*/
    public List<Device> getDeviceListParams(@Param("device_id") String device_id,@Param("name") String name, @Param("category_id") Integer category_id,@Param("location") String location);
    /**查询device列表*/
    public List<Device> getDeviceList();

    /**通过设备类别id获取设备类别名称**/
    public String getDeviceCategoryNameById(int id);

    /** 通过设备类别缩写获取设备类别名称 **/
    public String getDeviceCategoryNameByNu(String category_nu);

    /**获取设备类别**/
    public List<DeviceCategory> getDeviceCategory();
    /**获取经费来源**/
    public List<FundCategory> getFundCategory();
    /***库存数量+ */
    public Boolean updateInventoryNumber(@Param("device_id") String device_id,@Param("addNumber") Integer addNumber);
    /***库存数量- */
    public Boolean updateMinusInventoryNumber(@Param("device_id") String device_id,@Param("minusNumber") Integer minusNumber);

    /***增加出库数量+ */
    public Boolean updateAddOutwarehouseNumber(@Param("device_id") String device_id,@Param("addNumber") Integer addNumber);

    /***减少出库数量 */
    public Boolean updateMinusOutwarehouseNumber(@Param("device_id") String device_id,@Param("minusNumber") Integer minusNumber);

    /***获取已入库的所有设备编号*/
    public List<String> getAllDeviceIds();

    /*chh add*/
    /***减小借出数量*/
    public Boolean updateMinusLendNumber(@Param("device_id") String device_id,@Param("minusNumber") Integer minusNumber);

    /***增加借出数量+ */
    public Boolean updateAddLendNumber(@Param("device_id") String device_id,@Param("addNumber") Integer addNumber);

    /***减小归还数量*/
    public Boolean updateMinusReturnNumber(@Param("device_id") String device_id,@Param("minusNumber") Integer minusNumber);

    /***增加归还数量+ */
    public Boolean updateAddReturnNumber(@Param("device_id") String device_id,@Param("addNumber") Integer addNumber);
    /*增加报检报修数量*/
    public Boolean updateAddRepairwarehouseNumber(@Param("device_id") String device_id,@Param("addNumber") Integer addNumber);
    /*减少报检报修数量*/
    public Boolean updateMinusRepairwarehouseNumber(@Param("device_id")String device_id,@Param("minusNumber") Integer minusNumber);
	
	 /***减小报废数量*/
    public Boolean updateMinusScrapNumber(@Param("device_id") String device_id,@Param("minusNumber")  Integer minusNumber);

    /***增加报废数量+ */
    public Boolean updateAddScrapNumber(@Param("device_id") String device_id, @Param("addNumber") Integer addNumber);

    /*增加报检报修返回数量*/
    public Boolean updateAddRestockWarehouseNumber(@Param("device_id")String device_id,@Param("addNumber")Integer addNumber);
    /*减少报检报修返回数量*/
    public Boolean updateMinusRestockWarehouseNumber(@Param("device_id")String device_id,@Param("minusNumber")Integer minusNumber);

    /** 添加一个临时设备 */
    public Boolean addDeviceTemp(Device device);

    /** 删除一个临时设备 */
    public Boolean deleteDeviceTemp(@Param("id")Integer id, @Param("device_id")String device_id);

    /** 更新一个临时设备 */
    public Boolean updateDeviceTemp(Device device);

    /** 查询临时设备 */
    public List<Device> getDeviceTempList(@Param("id") Integer id, @Param("device_id")String device_id,
                                          @Param("name") String name, @Param("category_id") Integer category_id,
                                          @Param("location") String location, @Param("document_id")String document_id);

    /** 将临时仓库内的设备转移至真实仓库 */
    public Boolean translateDeviceTempToDevice(@Param("device_id") String device_id, @Param("document_id")String document_id);
}

