package com.example.dakudemo.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.dakudemo.annotation.MultiRequestBody;
import com.example.dakudemo.entity.*;
import com.example.dakudemo.exception.JWTException;
import com.example.dakudemo.service.*;
import com.example.dakudemo.util.JwtTokenUtils;
import com.example.dakudemo.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yfgan
 * @create 2021-12-29 11:19
 */
@RestController
@RequestMapping("/daku/admin")
public class AdminController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DocumentDeviceService documentDeviceService;
    @Autowired
    private InoutService inoutService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private RepairService repairService;
	@Autowired
    private DeviceStatusService deviceStatusService;
    @Autowired
    private ScrapService scrapService;
    @Autowired
    private ApproveService approveService;
    @Autowired
    private DeviceDocumentService  deviceDocumentService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtils jwtTokenUtil;
    @Autowired
    private RestockService restockService;

    @ApiOperation("修改device")
    @PutMapping("/updateDevice")
    public Boolean updateDevice(@RequestBody Device device){
        return deviceService.updateDevice(device);
    }


    // 入库单 3 开始
    @ApiOperation("获取in列表")
    @PostMapping("/inList")
    public List<Inout> getInList(@RequestBody Map<String,String> queryParms){
        String document_id = queryParms.get("document_id");
        Integer document_category_id = Integer.parseInt(queryParms.get("document_category_id"));
        Integer document_status = null;
        Integer buy_use_person_id = null;
        if(queryParms.containsKey("document_status")){
            document_status = Integer.parseInt(queryParms.get("document_status"));
        }
        if(queryParms.containsKey("buy_use_person_id")){
            buy_use_person_id = Integer.parseInt(queryParms.get("buy_use_person_id"));
        }
        List<Inout> inoutList = null;
        if("".equals(document_id)){
            inoutList =  inoutService.getInList(document_category_id);
        }else{
            inoutList = inoutService.getInListParams(document_id,document_category_id, document_status, buy_use_person_id);
        }
        for(Inout inout:inoutList){
            // List<Role> roleList = approveService.getSysDepartInfoByDocId(inout.getDocument_id(), inout.getDocument_status(),3, inout.getBuy_person_name());
            inout.setDoc_status_description(approveService.getStatusDes(inout.getDocument_status()));
//            if (ObjectUtils.isEmpty(roleList)){
//                continue;
//            }
//            inout.setRoleList(roleList);
        }
        return inoutList;
    }

    @ApiOperation("新增in")
    @PostMapping("/addIn")
    public Result addIn(@RequestHeader(value = "Authorization", required = true) String token,
                        @RequestBody Inout inout) throws JWTException {
        boolean isSuccess = true;

        /* 新增逻辑 2022/9/2 */
        Result result = new Result();
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("JWT不合法或用户不存在");
            return result;
        }
        inout.setBuy_use_person_id(user.getId());
        inout.setUser_id(user.getId());
        // 判断是否已经审批通过

        if (inout.getDocument_status().equals(2)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("该单据已经完成！");
            return result;
        }
        // 实际审批类型，新添加的审批类型：入库审批
        Integer realApproveType = 3;

        /* 逻辑结束 2022/9/2 */

// 提交入库单时不再进行设备信息添加, 而是转移到临时表里面
        Device device = inout.getDevice();
        device.setInventory_number(0);
        device.setDocument_id(inout.getDocument_id());
        List<Device> device_info = deviceService.getDeviceTempList(null, device.getDevice_id(), null,
                null, null, inout.getDocument_id());
        if (ObjectUtils.isEmpty(device_info)){
            deviceService.addDeviceTemp(device);
        }

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
        /* 新增逻辑 2022/9/2 */
        inout.setApprove_type(realApproveType);
        isSuccess = isSuccess && inoutService.addIn(inout);
        // 判断文件审批状态
        if (inout.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(inout.getUser_id(), realApproveType, 0);
            isSuccess = isSuccess && approveService.addNextLevelApprove(inout.getDocument_id(), inout.getUser_id(), 0,realApproveType, 3, approveProcess.getApprove_node());
        }

        result.setIsSuccess(true);
        result.setCode(1);
        result.setMsg("添加成功！");
        return result;


        /* 逻辑结束 2022/9/2 */


        // isSuccess = isSuccess && approveService.updateStockNoAuto(inout.getDocument_id(), 3);
        // return isSuccess;

    }

    @ApiOperation("修改in")
    @PutMapping("/updateIn")
    public Boolean updateIn(@RequestHeader(value = "Authorization", required = true) String token,@RequestBody Inout inout) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return false;
        }

        inout.setBuy_use_person_id(user.getId());
        String document_id = inout.getDocument_id();
        boolean isSuccess = true;
        isSuccess = approveService.fallbackNoAuto(document_id, 3);
        Integer realApproveType = 3;
        inout.setApprove_type(realApproveType);
        if (inout.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(inout.getUser_id(), inout.getApprove_type(), inout.getSystem_id());
            isSuccess = approveService.addNextLevelApprove(inout.getDocument_id(), inout.getUser_id(), 0,realApproveType, 3, approveProcess.getApprove_node());
        }
        // 更新入库单不能添加设备信息,而是更新临时表
        Device device = inout.getDevice();
        device.setInventory_number(0);
        device.setDocument_id(inout.getDocument_id());
        isSuccess = isSuccess && deviceService.updateDeviceTemp(device);
        return isSuccess && inoutService.updateIn(inout);
    }

    @ApiOperation("删除in")
    @DeleteMapping("/deleteIn/{id}")
    public Boolean deleteIn(@PathVariable("id") String id){
        int inId = Integer.parseInt(id);
        String document_id = inoutService.getInoutById(inId).getDocument_id();
        approveService.fallbackNoAuto(document_id, 3);
        return inoutService.deleteIn(inId);
    }
    // 入库单结束


    // 归还单 4 开始
    @ApiOperation("新增Return")
    @PostMapping("/addReturn")
    public Boolean addReturn(@RequestBody Return ret){

//        Boolean isSuccess = null;
//        Boolean isSuccess1 = null;
//        Boolean isSuccess2 = null;
//        for(DocumentDevice documentDevice: ret.getDocumentDeviceList()){
//            if(!ret.getDocument_id().equals(documentDevice.getDocument_id())){
//                isSuccess = Boolean.FALSE;
//                break;
//            }
//            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
//            String device_id = documentDevice.getDevice_id();
//            Integer deviceNumber = documentDevice.getDevice_number();
//            DocumentDevice documentDevice_lend = documentDeviceService.getDocumentDeviceByDocIdAndDeviceId(ret.getBorrow_document_id(),documentDevice.getDevice_id());
//            if(documentDevice_lend==null){
//                isSuccess=Boolean.FALSE;
//                break;
//            }
//            Integer lendNumber = documentDevice_lend.getDevice_number();
//            if(lendNumber < deviceNumber){
//                isSuccess=Boolean.FALSE;
//                break;
//            }
//            isSuccess1 = deviceService.updateAddReturnNumber(device_id, deviceNumber);
//            isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
//            if(!isSuccess || !(isSuccess1 && isSuccess2)){
//                isSuccess=Boolean.FALSE;
//                break;
//            }
//        }
//        isSuccess = Boolean.TRUE.equals(isSuccess) && returnService.addReturn(ret);
//        return isSuccess;
        boolean isSuccess;
        for(DocumentDevice documentDevice: ret.getDocumentDeviceList()){
            if(!ret.getDocument_id().equals(documentDevice.getDocument_id())){
                return false;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                return false;
            }
        }
        isSuccess = returnService.addReturn(ret);
        isSuccess = isSuccess && approveService.updateStockNoAuto(ret.getDocument_id(), 4);
        return isSuccess;

    }

    @ApiOperation("删除Return并复原相关数据")
    @DeleteMapping("/deleteReturn/{id}")
    public Boolean deleteReturn(@PathVariable("id") String id){
//        int RetId = Integer.parseInt(id);
//        String document_id = returnService.getReturnDocIdById(RetId);
//        Boolean isSuccess1 = null;
//        Boolean isSuccess2 = null;
//        Boolean isSuccess = null;
//        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
//        for(DocumentDevice documentDevice:documentDeviceList){
//            String device_id = documentDevice.getDevice_id();
//            Integer deviceNumber = documentDevice.getDevice_number();
//            isSuccess1 = deviceService.updateMinusReturnNumber(device_id, deviceNumber);
//            isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
//            isSuccess = isSuccess1 && isSuccess2;
//            if(!isSuccess){
//                break;
//            }
//        }
//        isSuccess = Boolean.TRUE.equals(isSuccess) && returnService.deleteReturn(RetId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
//        return isSuccess;
        int RetId = Integer.parseInt(id);
        String document_id = returnService.getReturnDocIdById(RetId);
        return approveService.fallbackNoAuto(document_id, 4) && returnService.deleteReturn(RetId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("修改Return,建议废弃,可以使用删除+添加完成")
    @PutMapping("/updateReturn")
    public Boolean updateReturn(@RequestBody Return ret){
//        int RetId = ret.getId();
//        Boolean isSuccess = deleteReturn(String.valueOf(RetId));
//        isSuccess = isSuccess && addReturn(ret);
//        return isSuccess;

        boolean isSuccess = approveService.fallbackNoAuto(ret.getDocument_id(), 4);
        return returnService.updateReturn(ret) && approveService.updateStockNoAuto(ret.getDocument_id(), 4);
    }

    @ApiOperation("获取Return列表")
    @PostMapping("/getReturnList")
    public List<Return> getReturnList(@RequestBody Map<String,String> queryParms){
        String document_id =null;
        Integer return_person_id= null;
        String return_person_name=null;
        if (!ObjectUtils.isEmpty(queryParms.get("document_id"))){
            document_id = queryParms.get("document_id");
        }
        if (!ObjectUtils.isEmpty(queryParms.get("return_person_id"))){
            return_person_id = Integer.parseInt(queryParms.get("return_person_id"));
        }
        if (!ObjectUtils.isEmpty(queryParms.get("return_person_name"))){
            return_person_id = Integer.parseInt(queryParms.get("return_person_name"));
        }
        List<Return> returnList = returnService.getReturnList(document_id, return_person_id, return_person_name);
        returnList = deviceDocumentService.setDevicesAndDocumentDevice(returnList);
//        for(Return ret:returnList){
//            ret.setReturn_person_name(userService.getUserNameById(ret.getReturn_person_id()));
//        }
        return returnList;
    }
    // 归还单结束


    //报检报修 6 开始
    @ApiOperation("获取报检报修列表")
    @PostMapping("/getRepairList")
    public List<Repair> getRepairList(@RequestBody Map<String,String> queryParms){
        String document_id =null;
        Integer check_repair_category_id= null;
        Integer document_status = null;
        if (!ObjectUtils.isEmpty(queryParms.get("document_id"))){
            document_id = queryParms.get("document_id");
        }
        if (!ObjectUtils.isEmpty(queryParms.get("check_repair_category_id"))){
            check_repair_category_id = Integer.parseInt(queryParms.get("check_repair_category_id"));
        }
        if (!ObjectUtils.isEmpty(queryParms.get("document_status"))){
            document_status = Integer.parseInt(queryParms.get("document_status"));
        }
        List<Repair> repairList = null;
        if(ObjectUtils.isEmpty(check_repair_category_id)){
            check_repair_category_id = null;
        }
        if("".equals(document_id) && ObjectUtils.isEmpty(check_repair_category_id)){
            repairList =  repairService.getRepairList();
        }else{
            repairList = repairService.getRepairListParams(document_id,check_repair_category_id, document_status);
        }
        for(Repair repair:repairList){
            repair.setDoc_status_description(approveService.getStatusDes(repair.getDocument_status()));
        }
        repairList = deviceDocumentService.setDevicesAndDocumentDevice(repairList);
        return repairList;
    }

    @ApiOperation("修改报检报修列表")
    @PutMapping("/updateRepair")
    public Boolean updateRepair(@RequestHeader(value = "Authorization", required = true) String token,
                                @RequestBody Repair repair) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return false;
        }
        // 新加逻辑
        Result resultTemp = approveService.isEnough(6, repair);
        if(! resultTemp.getIsSuccess()){
            return false;
        }
        // 逻辑结束
//        boolean isSuccess = approveService.fallbackNoAuto(repair.getDocument_id(), 6);
        boolean isSuccess = true;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(repair.getDocument_id());
        Integer realApproveType = 4;
        if (repair.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = approveService.addNextLevelApprove(repair.getDocument_id(), user.getId(), 0,realApproveType, 6, approveProcess.getApprove_node());
        }
        return repairService.updateRepair(repair);
    }

    @ApiOperation("删除报检报修")
    @DeleteMapping("/deleteRepair/{id}")
    public Boolean deleteRepair(@PathVariable("id") String id){
        int repairId = Integer.parseInt(id);
        Repair repair = repairService.getRepairById(repairId);
        if (repair.getDocument_status().equals(2)){
            // 拒绝删除
            return false;
        }
        String document_id = repair.getDocument_id();
        approveService.deleteApprove(null, document_id);
        return repairService.deleteRepair(repairId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("新增报检报修")
    @PostMapping("/addRepair")
    public Result addRepair(@RequestHeader(value = "Authorization", required = true) String token,
                             @RequestBody Repair repair) throws JWTException {

        boolean isSuccess = true;
        Result result = new Result();
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("JWT不合法或用户不存在");
            return result;
        }
        // 新加逻辑
        Result resultTemp = approveService.isEnough(6, repair);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束
        for(DocumentDevice documentDevice: repair.getDocumentDeviceList()){
            if(!repair.getDocument_id().equals(documentDevice.getDocument_id())){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据编号不匹配！");
                return result;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("设备信息添加失败！");
                return result;
            }
        }
        /* 新增逻辑 2022/9/9 */
        Integer realApproveType = 4;// 3为入库单审批，4为报检报修/归还/报废单提交
        //isSuccess = repairService.addRepair(repair);
        // 判断文件审批状态
        if (repair.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = isSuccess && approveService.addNextLevelApprove(repair.getDocument_id(), user.getId(), 0,realApproveType, 6, approveProcess.getApprove_node());
        }
        isSuccess = repairService.addRepair(repair);
        result.setIsSuccess(isSuccess);
        result.setCode(1);
        result.setMsg("单据添加成功！");
        return result;
    }

    @ApiOperation("获取报检报修类别")
    @GetMapping("/getRepairCategory")
    public List<RepairCategory> getRepairCategoryList(){
        return  repairService.getRepairCategoryList();
    }

    @ApiOperation("获取报检报修事由")
    @GetMapping("/getRepairCause")
    public List<RepairCause> getRepairCauseList(){
        return  repairService.getRepairCauseList();
    }
    //报检报修结束


	// 台账开始
	@ApiOperation("添加台账记录")
    @PostMapping("/addDeviceStatus")
    public Boolean addDeviceStatus(@RequestBody DeviceStatusRecord record){
        return deviceStatusService.addDeviceStatusRecord(record);
    }

    @ApiOperation("根据id删除台账记录")
    @DeleteMapping("/deleteDeviceStatusById/{id}")
    public Boolean deleteDeviceStatusById(@PathVariable("id") String id){
        return deviceStatusService.deleteDeviceStatusRecordById(Integer.parseInt(id));
    }

    @ApiOperation("根据log_id删除台账记录")
    @DeleteMapping("/deleteDeviceStatusByLogId/{log_id}")
    public Boolean deleteDeviceStatusByLogId(@PathVariable("log_id") String log_id){
        return deviceStatusService.deleteDeviceStatusRecordByLogId(log_id);
    }

    @ApiOperation("修改台账记录")
    @PutMapping("/updateDeviceStatus")
    public Boolean updateDeviceStatus(@RequestBody DeviceStatusRecord record){
        return deviceStatusService.updateDeviceStatusRecord(record);
    }

    @ApiOperation("根据id查询台账记录")
    @GetMapping("/getDeviceStatusById/{id}")
    public DeviceStatusRecord getDeviceStatusById(@PathVariable("id") String id){
        return deviceStatusService.getDeviceStatusRecordById(Integer.parseInt(id));
    }

    @ApiOperation("查询台账记录列表")
    @PostMapping("/getDeviceStatusList")
    public List<DeviceStatusRecord> getDeviceStatusList(@RequestBody Map<String,String> queryParms){
        String log_id = queryParms.get("log_id");
        String device_id = queryParms.get("device_id");
        String record_person_id_str = queryParms.get("record_person_id");
        Integer record_person_id = null;
        List<DeviceStatusRecord> recordList = null;
        if(!"".equals(record_person_id_str)){
            record_person_id =  Integer.parseInt(record_person_id_str);
        }
        recordList = deviceStatusService.getDeviceStatusRecordListParams(log_id, device_id, record_person_id);
        if(ObjectUtils.isEmpty(recordList)){
            return new ArrayList<DeviceStatusRecord>();
        }
        return recordList;
    }
    // 台账结束

    // 报废单 5 开始
    @ApiOperation("添加报废单")
    @PostMapping("/addScrap")
    public Result addScrap(@RequestHeader(value = "Authorization", required = true) String token,
                            @RequestBody Scrap scrap) throws JWTException {
        boolean isSuccess;
        Result result = new Result();
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("JWT不合法或用户不存在");
            return result;
        }

        // 新加逻辑
        Result resultTemp = approveService.isEnough(5, scrap);
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束
        for(DocumentDevice documentDevice: scrap.getDocumentDeviceList()){
            if(!scrap.getDocument_id().equals(documentDevice.getDocument_id())){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据编号不匹配！");
                return result;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("设备信息添加失败！");
                return result;
            }
        }
        Integer realApproveType = 4;// 3为入库单审批，4为报检报修/归还/报废单提交
        //isSuccess = repairService.addRepair(repair);
        // 判断文件审批状态
        if (scrap.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = approveService.addNextLevelApprove(scrap.getDocument_id(), user.getId(), 0,realApproveType, 5, approveProcess.getApprove_node());
        }
        isSuccess = scrapService.addScrap(scrap);
        result.setIsSuccess(isSuccess);
        result.setCode(1);
        result.setMsg("单据添加成功！");
        return result;
    }

    @ApiOperation("删除报废单")
    @DeleteMapping("/deleteScrap/{id}")
    public Boolean deleteScrap(@PathVariable("id") String id){
        int scrId = Integer.parseInt(id);
        Scrap scrap = scrapService.getScrapById(scrId);
        if (scrap.getDocument_status().equals(2)){
            // 拒绝删除
            return false;
        }
        String document_id = scrap.getDocument_id();
        approveService.deleteApprove(null, document_id);
        return scrapService.deleteScrap(scrId) && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("修改报废单,同lend")
    @PutMapping("/updateScrap")
    public Boolean updateScrap(@RequestHeader(value = "Authorization", required = true) String token,
                               @RequestBody Scrap scrap) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return false;
        }
        // 新加逻辑
        Result resultTemp = approveService.isEnough(5, scrap);
        if(! resultTemp.getIsSuccess()){
            return false;
        }
        // 逻辑结束
//        boolean isSuccess = approveService.fallbackNoAuto(scrap.getDocument_id(), 5);
        boolean isSuccess = true;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(scrap.getDocument_id());
        for(DocumentDevice documentDevice: scrap.getDocumentDeviceList()){
            if(!scrap.getDocument_id().equals(documentDevice.getDocument_id())){
                return false;
            }
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                return false;
            }
        }
        Integer realApproveType = 4;
        if (scrap.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = approveService.addNextLevelApprove(scrap.getDocument_id(), user.getId(), 0,realApproveType, 5, approveProcess.getApprove_node());
        }
        return scrapService.updateScrap(scrap);
    }

    @ApiOperation("获取报废单列表")
    @PostMapping("/getScrapList")
    public List<Scrap> getScrapList(@RequestBody Map<String,String> queryParms){
        String document_id =null;
        Integer document_status= null;
        if (!ObjectUtils.isEmpty(queryParms.get("document_id"))){
            document_id = queryParms.get("document_id");
        }
        if (!ObjectUtils.isEmpty(queryParms.get("document_status"))){
            document_status = Integer.parseInt(queryParms.get("document_status"));
        }

        List<Scrap> scrapList = scrapService.getScrapListParams(document_id, document_status);
        for(Scrap scrap:scrapList){
            scrap.setDoc_status_description(approveService.getStatusDes(scrap.getDocument_status()));
        }
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }

    @ApiOperation("获取报废单")
    @GetMapping("/getScrap/{id}")
    public Scrap getScrap(@PathVariable("id") String id){
        int ScpId = Integer.parseInt(id);
        Scrap scrap = scrapService.getScrapById(ScpId);
        scrap = deviceDocumentService.setSingleDevicesAndDocumentDevice(scrap);
        return scrap;
    }
    // 报废单结束

    public User getUserByJWT(String token) throws JWTException {
        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            throw new JWTException("JWT过期或不合法！");
        }
        if (StringUtils.isBlank(username) || StringUtils.isEmpty(username)) {
            return null;
        }
        User user = userService.getUserByUserName(username);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        return user;
    }

    @ApiOperation("添加报检报修返回单")
    @PostMapping("/addRestock")
    public Result addRestock(@RequestHeader(value = "Authorization", required = true) String token,
                             @RequestBody Restock restock) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        Result result = new Result();
        boolean isSuccess = true;
        if (ObjectUtils.isEmpty(user)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("JWT不合法或用户不存在");
            return result;
        }
        List<Repair> repairList = repairService.getRepairListParams(restock.getCheck_repair_document_id(), null,
                null);
        if(ObjectUtils.isEmpty(repairList)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("报检报修单据不存在！");
            return result;
        }
        if(!repairList.get(0).getDocument_status().equals(2)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("报检报修单据没有通过审批！");
            return result;
        }
        // 新加逻辑
        Result resultTemp = approveService.isBeyondForLendDoc(7, restock, restock.getCheck_repair_document_id());
        if(! resultTemp.getIsSuccess()){
            return resultTemp;
        }
        // 逻辑结束

        for(DocumentDevice documentDevice: restock.getDocumentDeviceList()){
            if(!restock.getDocument_id().equals(documentDevice.getDocument_id())){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据编号不匹配！");
                return result;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("设备信息添加失败！");
                return result;
            }
        }
        /* 新增逻辑 2022/9/9 */
        Integer realApproveType = 4;// 3为入库单审批，4为报检报修/归还/报废单提交
        //isSuccess = repairService.addRepair(repair);
        // 判断文件审批状态
        if (restock.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = isSuccess && approveService.addNextLevelApprove(restock.getDocument_id(), user.getId(), 0,realApproveType, 7, approveProcess.getApprove_node());
        }
        result = restockService.addRestock(restock);
        return result;
    }

    @ApiOperation("删除报检报修返回单")
    @GetMapping("/deleteRestock/{id}")
    public Boolean deleteRestock(@PathVariable Integer id){
        int restockId = id;
        List<Restock> restockList = restockService.getRestockListParams(restockId, null, null, null, null);
        if (ObjectUtils.isEmpty(restockList)){
            return false;
        }
        Restock restock = restockList.get(0);
        if (restock.getDocument_status().equals(2)){
            // 拒绝删除
            return false;
        }
        String document_id = restock.getDocument_id();
        approveService.deleteApprove(null, document_id);
        return restockService.deleteRestock(id).getIsSuccess() && documentDeviceService.deleteDocumentDeviceByDocId(document_id);
    }

    @ApiOperation("修改报检报修返回单")
    @PostMapping("/updateRestock")
    public Boolean updateRestock(@RequestHeader(value = "Authorization", required = true) String token,
                                @RequestBody Restock restock) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return false;
        }
        restock.setUser_id(user.getId());
        restock.setDocument_person_id(user.getId());
        // 新加逻辑
        Result resultTemp = approveService.isBeyondForLendDoc(7, restock, restock.getCheck_repair_document_id());
        if(! resultTemp.getIsSuccess()){
            return resultTemp.getIsSuccess();
        }
        // 逻辑结束
//        boolean isSuccess = approveService.fallbackNoAuto(restock.getDocument_id(), 7);
        boolean isSuccess = true;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(restock.getDocument_id());
        Integer realApproveType = 4;
        if (restock.getDocument_status().equals(1)){
            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(user.getId(), realApproveType, 0);
            isSuccess = approveService.addNextLevelApprove(restock.getDocument_id(), user.getId(), 0,realApproveType, 7, approveProcess.getApprove_node());
        }
        return isSuccess && restockService.updateRestock(restock).getIsSuccess();
    }

    @ApiOperation("删除报检报修返回单")
    @GetMapping("/getRestockList")
    public List<Restock> getRestockList(@RequestParam(value = "id", required = false) Integer id,
                                 @RequestParam(value = "document_id", required = false) String document_id,
                                 @RequestParam(value = "check_repair_document_id", required = false) String check_repair_document_id,
                                 @RequestParam(value = "document_person_id", required = false) Integer document_person_id,
                                        @RequestParam(value = "document_status", required = false) Integer document_status){
        List<Restock>  restockList = restockService.getRestockListParams(id, document_id, check_repair_document_id, document_person_id, document_status);
        restockList = deviceDocumentService.setDevicesAndDocumentDevice(restockList);
        for(Restock restock:restockList){
            restock.setDoc_status_description(approveService.getStatusDes(restock.getDocument_status()));
        }
        return restockList;
    }

}
