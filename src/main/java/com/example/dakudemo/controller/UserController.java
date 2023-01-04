package com.example.dakudemo.controller;


import com.example.dakudemo.annotation.MultiRequestBody;
import com.example.dakudemo.entity.*;
import com.example.dakudemo.service.*;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yfgan
 * @create 2021-12-29 11:19
 */
@RestController
@RequestMapping("/daku/user")
public class UserController {

    @Autowired
    private InoutService inoutService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocumentDeviceService documentDeviceService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LendService lendService;

    @Autowired
    private ReturnService returnService;

    @Autowired
    private ApproveService approveService;



     // 出库单 1 开始
    @ApiOperation("获取out列表")
    @PostMapping("/outList")
    public List<Inout> getOutList(@RequestBody Map<String,String> queryParms){
        String document_id = queryParms.get("document_id");
        Integer document_category_id = Integer.parseInt(queryParms.get("document_category_id"));
        String buy_use_person_id = queryParms.get("buy_use_person_id");
        List<Inout> inoutList = null;
        if("".equals(document_id) && "".equals(buy_use_person_id)){
            inoutList = inoutService.getOutList(document_category_id);
        }else{
            inoutList = inoutService.getOutListParams(document_id,document_category_id,Integer.parseInt(buy_use_person_id), null);
        }
        for(Inout inout:inoutList){
            inout.setDoc_status_description(approveService.getStatusDes(inout.getDocument_status()));
            List<Role> roleList = approveService.getSysDepartInfoByDocId(inout.getDocument_id(), inout.getDocument_status(),1, null);
            if (ObjectUtils.isEmpty(roleList)){
                continue;
            }
            inout.setRoleList(roleList);
        }

        return inoutList;
    }




    @ApiOperation("新增out")
    @PostMapping("/addOut")
    public Result addOut(@RequestBody Inout inout){
        Result result = new Result();
        if (inout.getDocument_status().equals(2)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("该单据已经完成！");
            return result;
        }
        boolean isSuccess = true;
        // 新加逻辑
        Result resultTemp = approveService.isEnough(1, inout, null);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束
        Integer realApproveType = approveService.whatRealApproveType(inout.getApprove_type(), inout.getDocumentDeviceList());
        for(DocumentDevice documentDevice: inout.getDocumentDeviceList()){
            if(!inout.getDocument_id().equals(documentDevice.getDocument_id())){
                isSuccess = Boolean.FALSE;
                break;
            }

            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if(!isSuccess){
                break;
            }
        }
        inout.setApprove_type(realApproveType);
        isSuccess = isSuccess && inoutService.addOut(inout);

        //ApproveProcess approveProcess = approveService.getFirstApproveProcessByApproveType(realApproveType);
        if (inout.getDocument_status().equals(1)){
            /* 11月更新 不再延续之前的流程 直接从头开始 */
//            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(inout.getUser_id(), inout.getApprove_type(), inout.getSystem_id());
            ApproveProcess approveProcess = approveService.getFirstApproveProcessByApproveType(realApproveType);
            isSuccess = isSuccess && approveService.addNextLevelApprove(inout.getDocument_id(), inout.getUser_id(), inout.getSystem_id(),realApproveType, 1, approveProcess.getApprove_node());
        }

        result.setIsSuccess(true);
        result.setCode(1);
        result.setMsg("添加成功！");
        return result;
    }

    @ApiOperation("修改out")
    @PutMapping("/updateOut")
    public Result updateOut(@RequestBody Inout inout){
        Result result = new Result();
        Inout preInout = inoutService.getInoutById(inout.getId());
        if (! preInout.getDocument_status().equals(0) || inout.getDocument_status().equals(2)){
            // 拒绝修改
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("单据不允许修改！");
            return result;
        }

        // 新加逻辑
        Result resultTemp = approveService.isEnough(1, inout, null);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束
        Integer realApproveType = approveService.whatRealApproveType(inout.getApprove_type(), inout.getDocumentDeviceList());
        inout.setApprove_type(realApproveType);
        boolean isSuccess = true;
        if (inout.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(inout.getUser_id(), inout.getApprove_type(), inout.getSystem_id());
            isSuccess = approveService.addNextLevelApprove(inout.getDocument_id(), inout.getUser_id(), inout.getSystem_id(),realApproveType, 1, approveProcess.getApprove_node());
        }
        isSuccess =  isSuccess && inoutService.updateOut(inout);
        result.setIsSuccess(isSuccess);
        result.setCode(1);
        result.setMsg("修改完成！");
        return result;
    }

    @ApiOperation("删除out")
    @DeleteMapping("/deleteOut/{id}")
    public Boolean deleteOut(@PathVariable("id") String id){
        int outId = Integer.parseInt(id);
        Inout inout = inoutService.getInoutById(outId);
        if (inout.getDocument_status().equals(2)){
            // 拒绝删除
            return false;
        }
        String document_id = inoutService.getInoutById(outId).getDocument_id();
        approveService.deleteApprove(null, document_id);
        return inoutService.deleteOut(outId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }
    // 出库单结束


    @ApiOperation("获取所有的部门主管")
    @GetMapping("/getUserInfoById/{id}")
    public String getUserInfoById(@PathVariable("id") String id){
        int userId = Integer.parseInt(id);
        return userService.getUserNameById(userId);
    }

    @ApiOperation("获取所有的设备编码")
    @GetMapping("/getAllDeviceIds")
    public List<String> getAllDeviceIds(){
        return deviceService.getAllDeviceIds();
    }
    @ApiOperation("通过设备id获取设备信息")
    @GetMapping("/getDeviceInfoByDeviceId/{deviceId}")
    public Device getDeviceInfoByDeviceId(@PathVariable("deviceId") String deviceId){
        return deviceService.getDeviceInfoByDeviceId(deviceId);
    }

    /*
        chh add
     */
    // 借出单 2 开始
    @ApiOperation("新增Lend")
    @PostMapping("/addLend")
    public Result addLend(@RequestBody Lend lend){
        Result result = new Result();
        if (lend.getDocument_status().equals(2)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("该单据已经完成！");
            return result;
        }
        boolean isSuccess = true;

        // 新加逻辑
        Result resultTemp = approveService.isEnough(2, null, lend);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束

        Integer realApproveType = approveService.whatRealApproveType(lend.getApprove_type(), lend.getDocumentDeviceList());
        for(DocumentDevice documentDevice: lend.getDocumentDeviceList()){
            if(!lend.getDocument_id().equals(documentDevice.getDocument_id())){
                isSuccess = Boolean.FALSE;
                break;
            }

            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if(!isSuccess){
                break;
            }
        }
        lend.setApprove_type(realApproveType);
        isSuccess = isSuccess && lendService.addLend(lend);
        //ApproveProcess approveProcess = approveService.getFirstApproveProcessByApproveType(realApproveType);
        if (lend.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(lend.getUser_id(), lend.getApprove_type(), lend.getSystem_id());
            isSuccess = isSuccess && approveService.addNextLevelApprove(lend.getDocument_id(), lend.getUser_id(), lend.getSystem_id(),realApproveType, 2, approveProcess.getApprove_node());
        }

        result.setIsSuccess(true);
        result.setCode(1);
        result.setMsg("添加成功！");
        return result;
    }


    @ApiOperation("删除Lend,同时删除Lend下对应document_device记录以及删除审批表")
    @DeleteMapping("/deleteLend/{id}")
    public Boolean deleteLend(@PathVariable("id") String id){
        int LendId = Integer.parseInt(id);
        Lend lend = lendService.getLendById(LendId);
        if (lend.getDocument_status().equals(2)){
            // 拒绝删除
            return false;
        }
        String document_id = lendService.getLendDocIdById(LendId);
        approveService.deleteApprove(null, document_id);
        return lendService.deleteLend(LendId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("修改Lend，若要修改对应设备信息，暂时只能删除重新创建")
    @PutMapping("/updateLend")
    public Result updateLend(@RequestBody Lend lend){
        Result result = new Result();
        Lend preLend = lendService.getLendById(lend.getId());
        if (! preLend.getDocument_status().equals(0) || lend.getDocument_status().equals(2)){
            // 拒绝修改
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("单据不允许修改！");
            return result;
        }
        // 新加逻辑
        Result resultTemp = approveService.isEnough(2, null, lend);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束
        Integer realApproveType = approveService.whatRealApproveType(lend.getApprove_type(), lend.getDocumentDeviceList());
        lend.setApprove_type(realApproveType);
        boolean isSuccess = true;
        if (lend.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(lend.getUser_id(), lend.getApprove_type(), lend.getSystem_id());
            isSuccess = approveService.addNextLevelApprove(lend.getDocument_id(), lend.getUser_id(), lend.getSystem_id(),realApproveType, 2, approveProcess.getApprove_node());
        }
        isSuccess =  isSuccess && lendService.updateLend(lend);
        result.setIsSuccess(isSuccess);
        result.setCode(1);
        result.setMsg("修改完成！");
        return result;
    }

    @ApiOperation("获取Lend列表")
    @PostMapping("/getLendList")
    public List<Lend> getLendList(@RequestBody Map<String,String> queryParms){
        String document_id = queryParms.get("document_id");
        Integer device_user_id = null;
        Integer document_status = null;
        if (!ObjectUtils.isEmpty(queryParms.get("device_user_id"))){
            device_user_id = Integer.parseInt(queryParms.get("device_user_id"));
        }
        if (!ObjectUtils.isEmpty(queryParms.get("document_status"))){
            document_status = Integer.parseInt(queryParms.get("document_status"));
        }

        List<Lend> lendList = lendService.getLendList(document_id, device_user_id, document_status);
        for(Lend lend:lendList){
            lend.setDoc_status_description(approveService.getStatusDes(lend.getDocument_status()));
            List<Role> roleList = approveService.getSysDepartInfoByDocId(lend.getDocument_id(), lend.getDocument_status(), 2, null);
            if (ObjectUtils.isEmpty(roleList)){
                continue;
            }
            lend.setRoleList(roleList);
        }
        return lendList;
    }
    // 借出单结束

    // ——————弃用——————————
    @ApiOperation("新增Return")
    @PostMapping("/addReturn")
    public Boolean addReturn(@RequestBody Return ret){
//        Return ret = new Return(addParms);
//        return returnService.addReturn(ret);
        Boolean isSuccess = null;
        // 新加逻辑
        Result resultTemp = approveService.isBeyondForLendDoc(5, ret, ret.getBorrow_document_id());
        if(! resultTemp.getIsSuccess()){
            return resultTemp.getIsSuccess();
        }
        // 逻辑结束
        for(DocumentDevice documentDevice: ret.getDocumentDeviceList()){
            if(!ret.getDocument_id().equals(documentDevice.getDocument_id())){
                isSuccess = Boolean.FALSE;
                break;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if(!isSuccess){
                break;
            }
        }
        isSuccess = returnService.addReturn(ret);
        return isSuccess;
    }

    @ApiOperation("删除Return")
    @DeleteMapping("/deleteReturn/{id}")
    public Boolean deleteReturn(@PathVariable("id") String id){
        int RetId = Integer.parseInt(id);
        String document_id = returnService.getReturnDocIdById(RetId);
        return returnService.deleteReturn(RetId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("修改Return,同lend")
    @PutMapping("/updateReturn")
    public Boolean updateReturn(@RequestBody Return ret){
        // 新加逻辑
        Result resultTemp = approveService.isBeyondForLendDoc(4, ret, ret.getBorrow_document_id());
        if(! resultTemp.getIsSuccess()){
            return resultTemp.getIsSuccess();
        }
        // 逻辑结束
        boolean isSuccess = approveService.fallbackNoAuto(ret.getDocument_id(), 4);
        return returnService.updateReturn(ret);
    }

    @ApiOperation("获取Return列表")
    @PostMapping("/getReturnList")
    public List<Return> getReturnList(@RequestBody Map<String,String> queryParms){
        String document_id = queryParms.get("document_id");
        String return_person_id = queryParms.get("return_person_id");
        String return_person_name = queryParms.get("return_person_name");
        if("".equals(return_person_id) && "".equals(document_id)){
            return returnService.getReturnListAll();
        }
        if("".equals(return_person_id)){
            return returnService.getReturnListById(document_id);
        }
        return returnService.getReturnList(document_id,Integer.parseInt(return_person_id), return_person_name);
    }
    // ——————弃用——————————
    /*
        device
     */
    @ApiOperation("获取device列表")
    @PostMapping("/getDeviceList")
    public List<Device> getDeviceList(@RequestBody Map<String,String> queryParms){
        String device_id = queryParms.get("device_id");
        String name = queryParms.get("name");
        String category_idString = queryParms.get("category_id");
        Integer category_id;
        if("".equals(category_idString)){
            category_id = null;
        }else{
            category_id =Integer.parseInt(category_idString);
        }
        String location = queryParms.get("location");
        if("".equals(device_id) && "".equals(name) && "".equals(category_idString) && "".equals(location)){
            return deviceService.getDeviceList();
        }
        return deviceService.getDeviceListParams(device_id,name,category_id,location);
    }

    @ApiOperation("新增device")
    @PostMapping("/addDevice")
    public Boolean addDevice(@RequestBody Device device){
        return deviceService.addDevice(device);
    }

    @ApiOperation("修改device")
    @PutMapping("/updateDevice")
    public Boolean updateDevice(@RequestBody Device device){
        return deviceService.updateDevice(device);
    }

    @ApiOperation("通过设备类别id获取设备类别名")
    @GetMapping("/getDeviceCategoryNameById/{id}")
    public String getDeviceCategoryNameById(@PathVariable("id") String id){
        int categoryId = Integer.parseInt(id);
        return deviceService.getDeviceCategoryNameById(categoryId);
    }
    @ApiOperation("通过设备类别nu获取设备类别名")
    @GetMapping("/getDeviceCategoryNameByNu/{category_nu}")
    public String getDeviceCategoryNameByNu(@PathVariable("category_nu") String category_nu){
        return deviceService.getDeviceCategoryNameByNu(category_nu);
    }

    @ApiOperation("获取设备类别")
    @GetMapping("/getDeviceCategory")
    public List<DeviceCategory> getDeviceCategory(){
        return deviceService.getDeviceCategory();
    }

    @ApiOperation("获取经费来源")
    @GetMapping("/getFundCategory")
    public List<FundCategory> getFundCategory(){
        return deviceService.getFundCategory();
    }
    @ApiOperation("通过document_id获取设备信息")
    @GetMapping("/getDocumentDeviceInfo/{document_id}")
    public List<DocumentDevice> getDocumentDeviceInfo(@PathVariable("document_id") String document_id) {
        return documentDeviceService.getDocumentDeviceInfo(document_id);
    }

    @ApiOperation("根据用户名获取用户id")
    @GetMapping("/getUserIdByDisplayName/{displayName}")
    public Integer getUserIdByDisplayName(@PathVariable("displayName") String displayName){
        Integer user_id = userService.getUserIdByDisplayName(displayName);
        if (ObjectUtils.isEmpty(user_id)){
            user_id = -1;
        }
        return user_id;
    }
}