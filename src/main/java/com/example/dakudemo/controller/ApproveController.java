package com.example.dakudemo.controller;

/*
 * @author:chh
 * @Date:2022-05-03-10:06
 * @Description:审批专用的控制器
 */

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.dakudemo.entity.*;
import com.example.dakudemo.exception.JWTException;
import com.example.dakudemo.service.*;
import com.example.dakudemo.util.JwtTokenUtils;
import com.example.dakudemo.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/daku/approve")
public class ApproveController {

    @Autowired
    private JwtTokenUtils jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private InoutService inoutService;

    @Autowired
    private LendService lendService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private RestockService restockService;

    @Autowired
    private ScrapService scrapService;

    /** =======================================用户接口：JWT==============================================*/
    @ApiOperation("用户获取自己提交的申请审批，后四个参数可选，第二个个参数是否筛选未审批 true:筛选，否则不筛选")
    @GetMapping("/getOnesPostApproves")
    public List<Approve> getOnesPostApproves(@RequestHeader(value = "Authorization", required = true) String token,
                                            @RequestParam(value = "document_id", required = false) String document_id,
                                             @RequestParam(value = "isNoApproved", required = false) boolean isNoApproved,
                                             @RequestParam(value = "isLatest",required = false) boolean isLatest,
                                             @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        List<Approve> approveList = null;
        // 根据用户信息查询用户提交的所有申请审批表
        if (ObjectUtils.isEmpty(isNoApproved) || !isNoApproved){
            approveList = approveService.getOnesPostApproves(user.getId(), document_id);
        }else {
            approveList = approveService.getOnesPostPendingApproves(user.getId(), document_id);
        }
        if (!ObjectUtils.isEmpty(isLatest) && isLatest){
            approveList = approveService.getOnesPostCurrentApproves(user.getId(), document_id);
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("用户获取自己最新的申请审批，后一个参数可选")
    @GetMapping("/getOnesCurrentApproves")
    public List<Approve> getOnesCurrentApproves(@RequestHeader(value = "Authorization", required = true) String token,
                                                @RequestParam(value = "document_id", required = false) String document_id,
                                                @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo) throws JWTException {
        // 根据token获取用户info
        User user = null;
        user = getUserByJWT(token);

        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据用户信息查询用户提交的最新申请审批表
        List<Approve> approveList = approveService.getOnesPostCurrentApproves(user.getId(), document_id);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("修改单据状态包含撤销申请以及提交申请")
    @GetMapping("/updateDocStatus")
    public Result updateDocStatus(@RequestHeader(value = "Authorization", required = true) String token,
                                   @RequestParam(value = "document_id", required = true) String document_id,
                                   @RequestParam(value = "system_id", required = false) Integer system_id,
                                   @RequestParam(value = "docType", required = true) Integer docType,
                                   @RequestParam(value = "docStatus", required = true) Integer docStatus) throws JWTException {
        Result result = new Result();
        // 不允许擅自修改状态为2
        if (docStatus.equals(2)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("非法传参：docStatus=2");
            return result;
        }
        if(docStatus.equals(1) && ObjectUtils.isEmpty(system_id)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("非法传参：system_id=null 且 docStatus=1");
            return result;
        }
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("JWT不合法或用户不存在");
            return result;
        }
        boolean isSuccess = true;
        Integer preStatus;
        Integer userId = user.getId();
        Integer approveType = null;
        Integer realApproveType = null;
        Integer documentLogicType = null; // 文档标志位，0为判断是否充足，1为判断提交是否符合原单据
        DeviceDocument document = null; //通用文档类型
        String documentIdOfDoc = null; // 文档关联文档id，只有documentLogicType=1才有意义
        // 判断用户是否是单据提交者
        if (docType.equals(1)){
            List<Inout> inoutList = inoutService.getOutListParams(document_id, null, user.getId(), null);
            if (ObjectUtils.isEmpty(inoutList)){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Inout inout = inoutList.get(0);
            preStatus = inout.getDocument_status();
            if (preStatus.equals(2)){
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            inout.setDocument_status(docStatus);

            // 新加逻辑
            documentLogicType = 0;
            document = inout;
//            Result resultTemp = approveService.isEnough(1, inout, null);
//            if(! resultTemp.getIsSuccess()){
//                return resultTemp;
//            }
            // 逻辑结束
            isSuccess =  inoutService.updateOut(inout);

            approveType = inout.getApprove_type();
            realApproveType = approveService.whatRealApproveType(inout.getApprove_type(), inout.getDocumentDeviceList());
        }else if (docType.equals(2)){
            List<Lend> lendList = lendService.getLendList(document_id, user.getId(), null);
            if (ObjectUtils.isEmpty(lendList)){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Lend lend = lendList.get(0);
            preStatus = lend.getDocument_status();
            if (preStatus.equals(2)){
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            lend.setDocument_status(docStatus);
            // 新加逻辑
            documentLogicType = 0;
            document = lend;
//            Result resultTemp = approveService.isEnough(2, null, lend);
//            if(! resultTemp.getIsSuccess()){
//                return resultTemp;
//            }
            // 逻辑结束
            isSuccess = lendService.updateLend(lend);

            approveType = lend.getApprove_type();
            realApproveType = approveService.whatRealApproveType(lend.getApprove_type(), lend.getDocumentDeviceList());
        }else if (docType.equals(3)) {
            // 入库单
            List<Inout> inoutList = inoutService.getInListParams(document_id, null, null, user.getId());
            if (ObjectUtils.isEmpty(inoutList)) {
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Inout inout = inoutList.get(0);
            inout.setBuy_use_person_id(user.getId());
            preStatus = inout.getDocument_status();
            if (preStatus.equals(2)) {
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            inout.setDocument_status(docStatus);
            isSuccess = inoutService.updateIn(inout);
            document = inout;

            approveType = 3;
            realApproveType = 3;
        }else if(docType.equals(6)){
            // 报检报修
            List<Repair> repairList = repairService.getRepairListParams(document_id, null, null);
            if (ObjectUtils.isEmpty(repairList)) {
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Repair repair = repairList.get(0);
            repair.setDocument_person_id(user.getId());
            preStatus = repair.getDocument_status();
            if (preStatus.equals(2)){
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            repair.setDocument_status(docStatus);

            // 新加逻辑
            documentLogicType = 0;
            document = repair;
//            Result resultTemp = approveService.isEnough(6, repair);
//            if(! resultTemp.getIsSuccess()){
//                return resultTemp;
//            }
            // 逻辑结束
            isSuccess =  repairService.updateRepair(repair);
            approveType = 4;
            realApproveType = 4;
        }else if(docType.equals(7)){
            // 报检报修归还
            List<Restock> restockList = restockService.getRestockListParams(null, document_id, null, null, null);
            if (ObjectUtils.isEmpty(restockList)) {
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Restock restock = restockList.get(0);
            restock.setDocument_person_id(user.getId());
            preStatus = restock.getDocument_status();
            if (preStatus.equals(2)){
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            restock.setDocument_status(docStatus);

            // 新加逻辑
            documentLogicType = 1;
            document = restock;
            documentIdOfDoc = restock.getCheck_repair_document_id();
//            Result resultTemp = approveService.isBeyondForLendDoc(7, restock, restock.getCheck_repair_document_id());
//            if(! resultTemp.getIsSuccess()){
//                return resultTemp;
//            }
            // 逻辑结束
            isSuccess =  restockService.updateRestock(restock).getIsSuccess();
            approveType = 4;
            realApproveType = 4;
        }else if(docType.equals(5)){
            // 报检报修归还
            List<Scrap> scrapList = scrapService.getScrapListParams(document_id, null);
            if (ObjectUtils.isEmpty(scrapList)) {
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("单据不存在或丢失！");
                return result;
            }
            Scrap scrap = scrapList.get(0);
            scrap.setDocument_person_id(user.getId());
            preStatus = scrap.getDocument_status();
            if (preStatus.equals(2)){
                // 已经完成审批流程，不允许修改
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("该单据已经完成审批，不允许修改！");
                return result;
            }
            scrap.setDocument_status(docStatus);



            // 新加逻辑
            documentLogicType = 0;
            document = scrap;
//            Result resultTemp = approveService.isEnough(5, scrap);
//            if(! resultTemp.getIsSuccess()){
//                return resultTemp;
//            }
            // 逻辑结束
            isSuccess =  scrapService.updateScrap(scrap);
            approveType = 4;
            realApproveType = 4;
        }else{
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("不支持该单据类型！");
            return result;
        }

        // 判断前序
        if (preStatus.equals(0) && docStatus.equals(1)){
            // 由编辑状态转审批状态
            if(!ObjectUtils.isEmpty(documentLogicType)){
                Result resultTemp;
                if (documentLogicType.equals(0)){
                    resultTemp = approveService.isEnough(docType, document);
                }else {
                    resultTemp = approveService.isBeyondForLendDoc(docType, document, documentIdOfDoc);
                }
                if(! resultTemp.getIsSuccess()){
                    return resultTemp;
                }
            }

            ApproveProcess approveProcess = approveService.getNextApproveProcessByUserIdAndApproveType(userId, approveType, system_id);
            isSuccess = isSuccess && approveService.addNextLevelApprove(document_id, userId, system_id,realApproveType, docType, approveProcess.getApprove_node());
        }else if(preStatus.equals(1) && docStatus.equals(0)){
            // 由审批状态转编辑状态
            isSuccess = approveService.deleteApprove(null, document_id);
        }
        result.setIsSuccess(true);
        result.setMsg("状态更新成功！");
        result.setCode(1);
        return result;
    }



    /** =======================================用户接口：user_id==============================================*/
    @ApiOperation("用户获取自己提交的申请审批，后两个参数可选，最后一个参数是否筛选未审批 true:筛选，否则不筛选")
    @GetMapping("/getOnesPostApprovesByUserId")
    public List<Approve> getOnesPostApprovesByUserId(@RequestParam(value = "user_id", required = true) Integer user_id,
                                                     @RequestParam(value = "document_id", required = false) String document_id,
                                                     @RequestParam(value = "isNoApproved", required = false) boolean isNoApproved,
                                                     @RequestParam(value = "isLatest",required = false) boolean isLatest,
                                                     @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo) {
        // 根据用户信息查询用户提交的所有申请审批表
        List<Approve> approveList = null;
        // 根据用户信息查询用户提交的所有申请审批表
        if (ObjectUtils.isEmpty(isNoApproved) || !isNoApproved){
            approveList = approveService.getOnesPostApproves(user_id, document_id);
        }else {
            approveList = approveService.getOnesPostPendingApproves(user_id, document_id);
        }
        if (!ObjectUtils.isEmpty(isLatest) && isLatest){
            approveList = approveService.getOnesPostCurrentApproves(user_id, document_id);
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }


    @ApiOperation("用户获取自己最新的申请审批，后一个参数可选")
    @GetMapping("/getOnesCurrentApprovesByUserId")
    public List<Approve> getOnesCurrentApprovesByUserId(@RequestParam(value = "user_id", required = true) Integer user_id,
                                                        @RequestParam(value = "document_id", required = false) String document_id,
                                                        @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo){
        // 根据用户信息查询用户提交的最新申请审批表
        List<Approve> approveList = approveService.getOnesPostCurrentApproves(user_id, document_id);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }
    /** =======================================用户接口：user_name==============================================*/
    @ApiOperation("用户获取自己提交的申请审批，后两个参数可选，最后一个参数是否筛选未审批 true:筛选，否则不筛选")
    @GetMapping("/getOnesPostApprovesByUsername")
    public List<Approve> getOnesPostApprovesByUsername(@RequestParam(value = "user_name", required = true) String user_name,
                                                       @RequestParam(value = "document_id", required = false) String document_id,
                                                       @RequestParam(value = "isNoApproved", required = false) boolean isNoApproved,
                                                       @RequestParam(value = "isLatest",required = false) boolean isLatest,
                                                       @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo) {
        // 根据用户名获取用户信息
        User user = userService.getUserByUserName(user_name);
        List<Approve> approveList = null;
        // 根据用户信息查询用户提交的所有申请审批表
        if (ObjectUtils.isEmpty(isNoApproved) || !isNoApproved){
            approveList = approveService.getOnesPostApproves(user.getId(), document_id);
        }else {
            approveList = approveService.getOnesPostPendingApproves(user.getId(), document_id);
        }
        if (!ObjectUtils.isEmpty(isLatest) && isLatest){
            approveList = approveService.getOnesPostCurrentApproves(user.getId(), document_id);
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("用户获取自己最新的申请审批，后一个参数可选")
    @GetMapping("/getOnesCurrentApprovesByUsername")
    public List<Approve> getOnesCurrentApprovesByUserId(@RequestParam(value = "user_name", required = true) String user_name,
                                                        @RequestParam(value = "document_id", required = false) String document_id,
                                                        @RequestParam(value = "addDocInfo", required = false) boolean addDocInfo){
        // 根据用户名获取用户信息
        User user = userService.getUserByUserName(user_name);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据用户信息查询用户提交的最新申请审批表
        List<Approve> approveList = approveService.getOnesPostCurrentApproves(user.getId(), document_id);
        if (!ObjectUtils.isEmpty(addDocInfo) && addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    /** =======================================审批人接口：JWT==============================================*/
    @ApiOperation("审批人获取所有到达自己申请审批，后两个参数可选")
    @GetMapping("/getApproverCurrentApproves")
    public List<Approve> getApproverCurrentApproves(@RequestHeader(value = "Authorization", required = true) String token,
                                                    @RequestParam(value = "isAll",defaultValue = "false", required = false) boolean isAll,
                                                    @RequestParam(value = "isNoApproved", defaultValue = "true",required = false) boolean isNoApproved,
                                                    @RequestParam(value = "addDocInfo", defaultValue = "true", required = false) boolean addDocInfo,
                                                    @RequestParam(value = "docType", required = false) Integer docType) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        List<Approve> approveList = null;
        // 根据审批人信息查询所有可见的申请审批表
        if (isAll){
            approveList = approveService.getApproverCurrentApproves(user.getId());
        }else if(isNoApproved){
            approveList = approveService.getApproverCurrentApproves(user.getId());
            approveList.removeIf(approve -> !approve.getApprove_status().equals(0));
        }else{
            approveList = approveService.getApproverProcessedApproves(user.getId());
        }
        if (!ObjectUtils.isEmpty(docType)){
            approveList.removeIf(approve -> !approve.getDocument_type().equals(docType));
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("审批人获取所有自己审批过的审批")
    @GetMapping("/getApproverProcessedApproves")
    public List<Approve> getApproverProcessedApproves(@RequestHeader(value = "Authorization", required = true) String token,
                                                      @RequestParam(value = "addDocInfo", defaultValue = "true", required = false) boolean addDocInfo,
                                                      @RequestParam(value = "docType", required = false) Integer docType) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproverProcessedApproves(user.getId());
        if (!ObjectUtils.isEmpty(docType)){
            approveList.removeIf(approve -> !approve.getDocument_type().equals(docType));
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }

        return approveList;
    }

    @ApiOperation("审批人获取审批链条")
    @GetMapping("/getApproveChainByDocIdAndApproverId")
    public List<Approve> getApproveChainByDocIdAndApproverId(@RequestHeader(value = "Authorization", required = true) String token,
                                                             @RequestParam(value = "document_id", required = true)String document_id,
                                                             @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproveChainByDocIdAndApproverId(document_id, user.getId());
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("审批人审批")
    @PostMapping("/approveIt")
    public boolean approveIt(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody Approve approve) throws JWTException {
        // 根据token获取用户info
        User user = getUserByJWT(token);
        if (ObjectUtils.isEmpty(user)){
            return false;
        }
        // 根据审批人信息查询所有审批过的申请审批表
        approve.setApprove_person_id(user.getId());
        return approveService.approveIt(approve);
    }
    /** =======================================审批人接口：user_id==============================================*/
    @ApiOperation("审批人获取所有到达自己申请审批，后两个参数可选")
    @GetMapping("/getApproverCurrentApprovesByUserId")
    public List<Approve> getApproverCurrentApprovesByUserId(@RequestParam(value = "user_id", required = true) Integer user_id,
                                                            @RequestParam(value = "isAll",defaultValue = "true", required = false) boolean isAll,
                                                            @RequestParam(value = "isNoApproved", defaultValue = "true",required = false) boolean isNoApproved,
                                                            @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        // 根据审批人信息查询所有可见的申请审批表
        List<Approve> approveList = null;
        // 根据审批人信息查询所有可见的申请审批表
        if (isAll){
            approveList = approveService.getApproverCurrentApproves(user_id);
        }else if(isNoApproved){
            approveList = approveService.getApproverCurrentApproves(user_id);
            approveList.removeIf(approve -> !approve.getApprove_status().equals(0));
        }else{
            approveList = approveService.getApproverProcessedApproves(user_id);
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("审批人获取所有自己申请过的审批")
    @GetMapping("/getApproverProcessedApprovesByUserId")
    public List<Approve> getApproverProcessedApprovesByUserId(@RequestParam(value = "user_id", required = true) Integer user_id,
                                                              @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproverProcessedApproves(user_id);
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }

        return approveList;
    }

    @ApiOperation("审批人获取所有自己申请过的审批")
    @GetMapping("/getApproveChainByDocIdAndApproverIdByUserId")
    public List<Approve> getApproveChainByDocIdAndApproverIdByUserId(@RequestParam(value = "user_id", required = true) Integer user_id,
                                                                     @RequestParam(value = "document_id", required = true)String document_id,
                                                                     @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproveChainByDocIdAndApproverId(document_id, user_id);
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    /** =======================================审批人接口：user_name==============================================*/
    @ApiOperation("审批人获取所有到达自己申请审批，后两个参数可选")
    @GetMapping("/getApproverCurrentApprovesByUsername")
    public List<Approve> getApproverCurrentApprovesByUsername(@RequestParam(value = "user_name", required = true) String user_name,
                                                              @RequestParam(value = "isAll",defaultValue = "true", required = false) boolean isAll,
                                                              @RequestParam(value = "isNoApproved", defaultValue = "true",required = false) boolean isNoApproved,
                                                              @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        User user = userService.getUserByUserName(user_name);
        if(ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据审批人信息查询所有可见的申请审批表
        List<Approve> approveList = null;
        // 根据审批人信息查询所有可见的申请审批表
        if (isAll){
            approveList = approveService.getApproverCurrentApproves(user.getId());
        }else if(isNoApproved){
            approveList = approveService.getApproverCurrentApproves(user.getId());
            approveList.removeIf(approve -> !approve.getApprove_status().equals(0));
        }else{
            approveList = approveService.getApproverProcessedApproves(user.getId());
        }
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }

    @ApiOperation("审批人获取所有自己申请过的审批")
    @GetMapping("/getApproverProcessedApprovesByUsername")
    public List<Approve> getApproverProcessedApprovesByUsername(@RequestParam(value = "user_name", required = true) String user_name,
                                                                @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        User user = userService.getUserByUserName(user_name);
        if(ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproverProcessedApproves(user.getId());
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }

        return approveList;
    }

    @ApiOperation("审批人获取所有自己申请过的审批")
    @GetMapping("/getApproveChainByDocIdAndApproverIdByUsername")
    public List<Approve> getApproveChainByDocIdAndApproverIdByUserId(@RequestParam(value = "user_name", required = true) String user_name,
                                                                     @RequestParam(value = "document_id", required = true)String document_id,
                                                                     @RequestParam(value = "addDocInfo", defaultValue = "false", required = false) boolean addDocInfo){
        User user = userService.getUserByUserName(user_name);
        if(ObjectUtils.isEmpty(user)){
            return null;
        }
        // 根据审批人信息查询所有审批过的申请审批表
        List<Approve> approveList = approveService.getApproveChainByDocIdAndApproverId(document_id, user.getId());
        approveList = approveService.addInfoForApproves(approveList);
        if (addDocInfo){
            approveList = approveService.addDocInfoForApproves(approveList);
        }
        return approveList;
    }


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

    @ExceptionHandler(JWTException.class)
    @ResponseStatus(value = HttpStatus.FOUND, reason = "JWT错误")
    public String handleJWTException(Exception ex){
         return ex.getMessage();
    }


    @ApiOperation("获取审批单的可审批的所有审批人")
    @GetMapping("/getApproverByOneApprove")
    public Result getApproverByOneApprove(@RequestParam(value = "approve_id", required = true) Integer approve_id){
        Result result = new Result();
        List<Approve> approveList = approveService.getApprove(approve_id, null, null, null, null, null, null, null, null);
        if (ObjectUtils.isEmpty(approveList)){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("此审批不存在！");
            return result;
        }
        Approve approve = approveList.get(0);
        return approveService.getApproversByApprove(approve);
    }
}