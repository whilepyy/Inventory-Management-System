package com.example.dakudemo.controller;

import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Inout;
import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Repair;
import com.example.dakudemo.entity.Scrap;
import com.example.dakudemo.mapper.DeviceMapper;
import com.example.dakudemo.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yfgan
 * @create 2021-12-29 11:19
 */
@RestController
@RequestMapping("/daku/userHead")
public class UserHeadController {
    @Autowired
    private OutApprovalService  outApprovalService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private LendService lendService;
    @Autowired
    private LendApprovalService lendApprovalService;
    @Autowired
    private DocumentDeviceService documentDeviceService;
    @Autowired
    private RepairApprovalService repairApprovalService;
	@Autowired
    private UserService userService;
	@Autowired
    private ScrapApprovalService scrapApprovalService;


    @ApiOperation("获取出库审批列表")
    @PostMapping("/outApprovalList")
    public List<Inout> getOutApprovalList(@RequestBody Map<String,String> queryParms){
        String approve_status = queryParms.get("approve_status");
        String approve_person_id = queryParms.get("approve_person_id");
        if("".equals(approve_person_id)){
            return outApprovalService.getOutApprovalListAll(approve_status);

        }
        return outApprovalService.getOutApprovalList(approve_status,Integer.parseInt(approve_person_id));
    }

    @ApiOperation("修改出库申请")
    @PutMapping("/updateOutApproval/{id}/{approve_status}/{device_id}/{device_number}")
    public Boolean updateOutApproval(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status,@PathVariable("device_id") String device_id,@PathVariable("device_number") String device_number){
        int outApprovalId = Integer.parseInt(id);
        Integer deviceNumber = Integer.parseInt(device_number);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isUpdate = outApprovalService.updateOutApproval(outApprovalId,approve_status,approve_time);
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        if(isUpdate && "0".equals(approve_status)){
            isSuccess1 = deviceService.updateAddOutwarehouseNumber(device_id,deviceNumber);
            isSuccess2 = deviceService.updateMinusInventoryNumber(device_id,deviceNumber);
        }
        if(isUpdate && "1".equals(approve_status)){
            isSuccess1 = true;
            isSuccess2 = true;
        }
        return isSuccess1 && isSuccess2;
    }

    @ApiOperation("删除出库申请")
    @DeleteMapping("/deleteOutApproval/{id}")
    public Boolean deleteOutApproval(@PathVariable("id") String id){
        int outApprovalId = Integer.parseInt(id);
        return outApprovalService.deleteOutApproval(outApprovalId);
    }
    /*chh add*/
    @ApiOperation("获取借出审批列表")
    @PostMapping("/LendApprovalList")
    public List<Lend> getLendApprovalList(@RequestBody Map<String,String> queryParms){
        String approve_status = queryParms.get("approve_status");
        String approve_person_id = queryParms.get("approve_person_id");
        if("".equals(approve_person_id)){
            return lendApprovalService.getLendApprovalListAll(approve_status);
        }
        return lendApprovalService.getLendApprovalList(approve_status,Integer.parseInt(approve_person_id));
    }

    @ApiOperation("修改单个设备借出申请")
    @PutMapping("/updateLendApprovalOneDevice/{id}/{approve_status}/{device_id}/{device_number}")
    public Boolean updateLendApprovalOneDevice(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status,@PathVariable("device_id") String device_id,@PathVariable("device_number") String device_number){
        int lendApprovalId = Integer.parseInt(id);
        Integer deviceNumber = Integer.parseInt(device_number);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isUpdate = lendApprovalService.updateLendApproval(lendApprovalId,approve_status,approve_time);
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        if(isUpdate && "0".equals(approve_status)){
            isSuccess1 = deviceService.updateAddLendNumber(device_id,deviceNumber);
            isSuccess2 = deviceService.updateMinusInventoryNumber(device_id,deviceNumber);
        }
        if(isUpdate && "1".equals(approve_status)){
            isSuccess1 = true;
            isSuccess2 = true;
        }
        return isSuccess1 && isSuccess2;
    }

    @ApiOperation("修改借单下所有设备借出申请")
    @PutMapping("/updateLendApprovalAllDevices/{id}/{approve_status}")
    public Boolean updateLendApprovalAllDevices(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status) {
        int lendApprovalId = Integer.parseInt(id);
        String document_id = lendApprovalService.getDocumentIdById(lendApprovalId);
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        Boolean isSuccess = null;

        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            Boolean isUpdate = lendApprovalService.updateLendApproval(lendApprovalId, approve_status, approve_time);
            if(!isUpdate){
                isSuccess = Boolean.FALSE;
            }
            isSuccess = Boolean.TRUE;
            if ("0".equals(approve_status)) {
                for (DocumentDevice documentDevice : documentDeviceList) {
                    String device_id = documentDevice.getDevice_id();
                    Integer deviceNumber = documentDevice.getDevice_number();
                    isSuccess1 = deviceService.updateAddLendNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                    isSuccess = isSuccess1 && isSuccess2;
                    if(!isSuccess){break;}
                }
            }else if("1".equals(approve_status)){
                isSuccess = true;
            }
        } else {
          isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }

    @ApiOperation("删除借出申请,建议弃用于UserController里面的deleteLend方法重叠")
    @DeleteMapping("/deleteLendApproval/{id}")
    public Boolean deleteLendApproval(@PathVariable("id") String id){
        int lendApprovalId = Integer.parseInt(id);
        return lendApprovalService.deleteLendApproval(lendApprovalId);
    }


}
