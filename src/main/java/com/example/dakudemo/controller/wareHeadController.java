package com.example.dakudemo.controller;

import com.example.dakudemo.entity.*;
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
@RequestMapping("/daku/wareHead")
public class wareHeadController {
    @Autowired
    private RepairApprovalService  repairApprovalService;
    @Autowired
    private ReturnApprovalService  returnApprovalService;
    @Autowired
    private DeviceService  deviceService;
    @Autowired
    private UserService  userService;
    @Autowired
    private ScrapApprovalService  scrapApprovalService;
    @Autowired
    private DocumentDeviceService  documentDeviceService;


    /*报检报修审批单*/
    @ApiOperation("获取报检报修审批列表")
    @PostMapping("/getRepairApprovalList")
    public List<Repair> getRepairApprovalList(@RequestBody Map<String,String> queryParms){
        String approve_status = queryParms.get("approve_status");
        String approve_person_id = queryParms.get("approve_person_id");
        if("".equals(approve_person_id)){
            return repairApprovalService.getRepairApprovalListAll(approve_status);
        }
        return repairApprovalService.getRepairApprovalList(approve_status,Integer.parseInt(approve_person_id));
    }
    @ApiOperation("修改报检报修申请")
    @PutMapping("/updateRepairApproval/{id}/{approve_status}/{device_id}/{device_number}")
    public Boolean updateRepairApproval(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status,@PathVariable("device_id") String device_id,@PathVariable("device_number") String device_number){
        int repairApprovalId = Integer.parseInt(id);
        Integer deviceNumber = Integer.parseInt(device_number);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isUpdate = repairApprovalService.updateRepairApproval(repairApprovalId,approve_status,approve_time);
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        if(isUpdate && "0".equals(approve_status)){
            isSuccess1 = deviceService.updateAddRepairwarehouseNumber(device_id,deviceNumber);
            isSuccess2 = deviceService.updateMinusInventoryNumber(device_id,deviceNumber);
        }
        return isSuccess1 && isSuccess2;
    }

    @ApiOperation("删除报检报修申请")
    @DeleteMapping("/deleteRepairApproval/{id}")
    public Boolean deleteRepairApproval(@PathVariable("id") String id){
        int repairApprovalId = Integer.parseInt(id);
        return repairApprovalService.deleteRepairApproval(repairApprovalId);
    }
    /*归还审批单*/
    @ApiOperation("获取归还审批单列表")
    @PostMapping("/getReturnApprovalList")
    public List<Return> getReturnApprovalList(@RequestBody Map<String,String> queryParms){
        String approve_status = queryParms.get("approve_status");
        String approve_person_id = queryParms.get("approve_person_id");
        if("".equals(approve_person_id)){
            return returnApprovalService.getReturnApprovalListAll(approve_status);
        }
        return returnApprovalService.getReturnApprovalList(approve_status,Integer.parseInt(approve_person_id));
    }

    @ApiOperation("删除归还单申请")
    @DeleteMapping("/deleteReturnApproval/{id}")
    public Boolean deleteReturnApproval(@PathVariable("id") String id){
        int returnApprovalId = Integer.parseInt(id);
        return returnApprovalService.deleteReturnApproval(returnApprovalId);
    }

    @ApiOperation("修改单个设备归还申请")
    @PutMapping("/updateReturnApprovalOneDevice/{id}/{approve_status}/{device_id}/{device_number}")
    public Boolean updateReturnApprovalOneDevice(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status,@PathVariable("device_id") String device_id,@PathVariable("device_number") String device_number){
        int lendApprovalId = Integer.parseInt(id);
        Integer deviceNumber = Integer.parseInt(device_number);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isUpdate = returnApprovalService.updateReturnApproval(lendApprovalId,approve_status,approve_time);
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        if(isUpdate && "0".equals(approve_status)){
            isSuccess1 = deviceService.updateAddReturnNumber(device_id,deviceNumber);
            isSuccess2 = deviceService.updateInventoryNumber(device_id,deviceNumber);
        }
        return isSuccess1 && isSuccess2;
    }

    @ApiOperation("修改借单下所有设备归还申请")
    @PutMapping("/updateReturnApprovalAllDevices/{id}/{approve_status}")
    public Boolean updateReturnApprovalAllDevices(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status) {
        int lendApprovalId = Integer.parseInt(id);
        String document_id = returnApprovalService.getDocumentIdById(lendApprovalId);
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        Boolean isSuccess = null;

        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            Boolean isUpdate = returnApprovalService.updateReturnApproval(lendApprovalId, approve_status, approve_time);
            if(!isUpdate){
                isSuccess = Boolean.FALSE;
            }
            isSuccess = Boolean.TRUE;
            if ("0".equals(approve_status)) {
                for (DocumentDevice documentDevice : documentDeviceList) {
                    String device_id = documentDevice.getDevice_id();
                    Integer deviceNumber = documentDevice.getDevice_number();
                    isSuccess1 = deviceService.updateAddReturnNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
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
    /*报废审批单*/
    @ApiOperation("获取报废审批列表")
    @PostMapping("/ScrapApprovalList")
    public List<Scrap> getScrapApprovalList(@RequestBody Map<String,String> queryParms){
        String approve_status = queryParms.get("approve_status");
        String approve_person_id = queryParms.get("approve_person_id");
        List<Scrap> scrapList = new ArrayList<>();
        if("".equals(approve_person_id)){
           scrapList = scrapApprovalService.getScrapApprovalListAll(approve_status);
        }else{
            scrapList = scrapApprovalService.getScrapApprovalList(approve_status, Integer.parseInt(approve_person_id));
        }
        //List<Scrap> lastScrapList = new ArrayList<>();
        //scrapList.forEach(scrap -> {
            //Integer user_department_id = userService.getDepartmentIdByUserId(scrap.getDocument_person_id());
            //Integer head_department_id = userService.getDepartmentIdByUserId(scrap.getApprove_person_id());
            //if(Objects.equals(user_department_id, head_department_id)){//同一个部门
                //lastScrapList.add(scrap);
            //}
        //});
        //return lastScrapList;
        return scrapList;
    }

    @ApiOperation("修改单个设备报废申请")
    @PutMapping("/updateScrapApprovalOneDevice/{id}/{approve_status}/{device_id}/{device_number}")
    public Boolean updateScrapApprovalOneDevice(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status,@PathVariable("device_id") String device_id,@PathVariable("device_number") String device_number){
        int scrapApprovalId = Integer.parseInt(id);
        Integer deviceNumber = Integer.parseInt(device_number);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isUpdate = scrapApprovalService.updateScrapApproval(scrapApprovalId,approve_status,approve_time);
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        if(isUpdate && "0".equals(approve_status)){
            isSuccess1 = deviceService.updateAddScrapNumber(device_id,deviceNumber);
            isSuccess2 = deviceService.updateMinusInventoryNumber(device_id,deviceNumber);
        }
        if(isUpdate && "1".equals(approve_status)){
            isSuccess1 = true;
            isSuccess2 = true;
        }
        return Boolean.TRUE.equals(isSuccess1) && isSuccess2;
    }

    @ApiOperation("修改报废单下所有设备报废申请")
    @PutMapping("/updateScrapApprovalAllDevices/{id}/{approve_status}")
    public Boolean updateScrapApprovalAllDevices(@PathVariable("id") String id,@PathVariable("approve_status") String approve_status) {
        int scrapApprovalId = Integer.parseInt(id);
        String document_id = scrapApprovalService.getDocumentIdById(scrapApprovalId);
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        Boolean isSuccess1 = null;
        Boolean isSuccess2 = null;
        Boolean isSuccess = null;

        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            Boolean isUpdate = scrapApprovalService.updateScrapApproval(scrapApprovalId, approve_status, approve_time);
            if(!isUpdate){
                isSuccess = Boolean.FALSE;
            }
            isSuccess = Boolean.TRUE;
            if ("0".equals(approve_status)) {
                for (DocumentDevice documentDevice : documentDeviceList) {
                    String device_id = documentDevice.getDevice_id();
                    Integer deviceNumber = documentDevice.getDevice_number();
                    isSuccess1 = deviceService.updateAddScrapNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                    isSuccess = isSuccess1 && isSuccess2;
                    if(!isSuccess){break;}
                }
            }
        } else {
            isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }

    @ApiOperation("删除报废单申请")
    @DeleteMapping("/deleteScrapApproval/{id}")
    public Boolean deleteScrapApproval(@PathVariable("id") String id){
        int scrapApprovalId = Integer.parseInt(id);
        return scrapApprovalService.deleteScrapApproval(scrapApprovalId);
    }


}
