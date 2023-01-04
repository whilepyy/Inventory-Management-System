package com.example.dakudemo.service;

import com.example.dakudemo.entity.*;
import com.example.dakudemo.mapper.ApproveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.text.SimpleDateFormat;
import java.util.*;

/*
 * @author:chh
 * @Date:2022-04-28-17:30
 * @Description:审批服务实现
 */
@Service
public class ApproveServiceImpl implements  ApproveService {

    @Value("${app.approveType.amount}")
    private double amountLimit;

    @Autowired
    private ApproveMapper approveMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentDeviceService documentDeviceService;

    @Autowired
    private LendService lendService;

    @Autowired
    private InoutService inoutService;

    @Autowired
    private ReturnService returnService;

    @Autowired
    private ScrapService scrapService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private RestockService restockService;

    @Autowired
    private DeviceDocumentService deviceDocumentService;

    /**
     * 添加审批表
     */
    public boolean addApprove(Approve approve) {
        if (ObjectUtils.isEmpty(approve.getDocument_id())) {
            return false;
        }
        return approveMapper.addApprove(approve);
    }

    /**
     * 更新审批表
     */
    public boolean updateApprove(Approve approve) {
        if (ObjectUtils.isEmpty(approve.getDocument_id())) {
            return false;
        }
        return approveMapper.updateApprove(approve);
    }

    /**
     * 根据id或者doc_id删除审批表
     */
    public boolean deleteApprove(Integer id, String document_id) {
        return approveMapper.deleteApprove(id, document_id);
    }

    /**
     * 查询审批表
     */
    public List<Approve> getApprove(Integer id, String document_id,
                                    Integer approve_type,Integer document_type,
                                    Integer user_id,Integer system_id,
                                    Integer approve_person_id,
                                    Integer approve_node, Integer approve_status) {
        if (
                ObjectUtils.isEmpty(id) &&
                        ObjectUtils.isEmpty(document_id) &&
                        ObjectUtils.isEmpty(approve_type) &&
                        ObjectUtils.isEmpty(document_type) &&
                        ObjectUtils.isEmpty(user_id) &&
                        ObjectUtils.isEmpty(system_id) &&
                        ObjectUtils.isEmpty(approve_person_id) &&
                        ObjectUtils.isEmpty(approve_node) &&
                        ObjectUtils.isEmpty(approve_status)
        ) {
            return null;
        }
        return approveMapper.getApprove(id, document_id, approve_type, document_type, user_id, system_id, approve_person_id, approve_node, approve_status);
    }

    /**
     * 根据前端传入的类型与设备列表
     */
    public Integer whatRealApproveType(Integer inputType, List<DocumentDevice> documentDeviceList) {
        if (inputType == 0) {
            return 0; //如果为计划内直接不算了
        }
        double sumPrice = 0.0;
        for (DocumentDevice documentDevice : documentDeviceList) {
            // 获取设备id
            String device_id = documentDevice.getDevice_id();
            // 获取设备数量
            Integer device_number = documentDevice.getDevice_number();
            // 获取设备信息
            Device device = deviceService.getDeviceInfoByDeviceId(device_id);
            // 获取单价
            double unitPrice = Double.parseDouble(device.getUnit_price());
            // 合计总价
            sumPrice += device_number * unitPrice;
        }
        if (sumPrice >= amountLimit) {
            return 1;   //计划内>=限定金额
        }
        return 2; //计划内<限定金额
    }

    /**
     * 生成下一个级别的审批表
     */
    public boolean addNextLevelApprove(String document_id, Integer user_id, Integer system_id,Integer approve_type, Integer document_type, Integer next_node) {
        if (ObjectUtils.isEmpty(document_id) || ObjectUtils.isEmpty(approve_type) || ObjectUtils.isEmpty(document_type) ||
                ObjectUtils.isEmpty(next_node) || ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(system_id)) {
            return false;
        }
        //Integer next_node = current_node + 1;// 此处可以查询下一个更大的节点
        Approve approve = new Approve(null, document_id, approve_type, document_type, user_id,
                system_id, null, next_node, 0, null, null);
        return addApprove(approve);
    }

    /**
     * 个人查询自己提交的审批表
     */
    public List<Approve> getOnesPostApproves(Integer user_id, String document_id) {
        return approveMapper.getApprove(null, document_id, null, null, user_id,
                null, null, null, null);
    }

    /**
     * 个人查询自己提交的当前最新审批表
     */
    public List<Approve> getOnesPostCurrentApproves(Integer user_id, String document_id) {
        return approveMapper.getOnesCurrentApproves(user_id, document_id);
    }

    /**
     * 个人查询自己提交的未审批表
     */
    public List<Approve> getOnesPostPendingApproves(Integer user_id, String document_id) {
        return getApprove(null, document_id, null, null, user_id,
                null, null, null, 0);
    }

    /**
     * 审批人查询到达自己审批流程的审批表
     */
    public List<Approve> getApproverCurrentApproves(Integer approver_id){
        List<Role> roleList = approveMapper.getRolesByUserId(approver_id);
        //定义返回结果list
        List<Approve> lastApproveList = new ArrayList<>();
        for (Role role:roleList){
            // 获取角色可视范围
            VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(role.getId());
            // 获取角色对应审批流程
            List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByRoleId(role.getId());
            // 获取审批人的系统和部门
            Integer approver_system_id = role.getSystem_id();
            Integer approver_department_id = role.getDepartment_id();
            // 遍历审批节点
            for (ApproveProcess approveProcess:approveProcessList){
                List<Approve> approveList = null;
                // approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), approveProcess.getApprove_node(), null, )
                if (visibleRange.isAll()) {
                    approveList = approveMapper.getApprove(null, null, approveProcess.getApprove_type(), null, null,
                            null, null, approveProcess.getApprove_node(), null);
                } else if (visibleRange.isDepartment()) {
                    approveList = approveMapper.getApprovesBySysAndDepartment (approveProcess.getApprove_type(), approveProcess.getApprove_node(), null, approver_department_id);
                } else if (visibleRange.isSystem() ) {
                    approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), approveProcess.getApprove_node(), approver_system_id, approver_department_id);
                }
                if (!ObjectUtils.isEmpty(approveList)) {
                    lastApproveList.addAll(approveList);
                }
            }
        }
        return lastApproveList;
    }
//    public List<Approve> getApproverCurrentApproves(Integer approver_id) {
//        // 获取审批人身份信息,以及可见范围
//        User roleUser = userService.getRolesByUserName(userService.getUserNameById(approver_id));
//        List<Role> roleList = roleUser.getRoles();
//        Integer approve_department_id = userService.getDepartmentIdByUserId(approver_id);
//
//        //定义返回结果list
//        List<Approve> lastApproveList = new ArrayList<>();
//        // 遍历角色
//        for (Role role : roleList) {
//            // 获取审批人系统
//            Integer approve_system_id = role.getSystem_id();
//            // 获取角色可视范围
//            VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(role.getId());
//            // 获取角色对应审批流程
//            List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByRoleId(role.getId());
//            // 遍历流程
//            for (ApproveProcess approveProcess : approveProcessList) {
//                Integer node = approveProcess.getApprove_node();
//                // 获取抵达节点的审批表
//                List<Approve> approveList = null;
//                if (visibleRange.isAll()) {
//                    approveList = approveMapper.getApprove(null, null, approveProcess.getApprove_type(), null, null,
//                            null, null, node, null);
//                } else if (visibleRange.isDepartment()) {
//                    approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), node, null, approve_department_id);
//                } else if (visibleRange.isSystem() ) {
//                    approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), node, approve_system_id, approve_department_id);
//                }
//                if (!ObjectUtils.isEmpty(approveList)) {
//                    lastApproveList.addAll(approveList);
//                }
//            }
//        }
//        return lastApproveList;
//    }
//    public List<Approve> getApproverCurrentApproves(Integer approver_id) {
//        // 获取审批人身份信息,以及可见范围
//        User roleUser = userService.getRolesByUserName(userService.getUserNameById(approver_id));
//        List<Role> roleList = roleUser.getRoles();
//        Integer approve_department_id = userService.getDepartmentIdByUserId(approver_id);
//        Integer approve_system_id = userService.getSystemIdByUserId(approver_id);
//        //定义返回结果list
//        List<Approve> lastApproveList = new ArrayList<>();
//        // 遍历角色
//        for (Role role : roleList) {
//            // 获取角色可视范围
//            VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(role.getId());
//            // 获取角色对应审批流程
//            List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByRoleId(role.getId());
//            // 遍历流程
//            for (ApproveProcess approveProcess : approveProcessList) {
//                Integer node = approveProcess.getApprove_node();
//                // 获取抵达节点的审批表
//                List<Approve> approveList = null;
//                if (visibleRange.isAll()) {
//                    approveList = approveMapper.getApprove(null, null, approveProcess.getApprove_type(), null, null, null, node, null);
//                } else if (visibleRange.isDepartment()) {
//                    approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), node, null, approve_department_id);
//                } else if (visibleRange.isSystem()) {
//                    approveList = approveMapper.getApprovesBySysAndDepartment(approveProcess.getApprove_type(), node, approve_system_id, approve_department_id);
//                }
//                if (!ObjectUtils.isEmpty(approveList)) {
//                    lastApproveList.addAll(approveList);
//                }
//            }
//        }
//        return lastApproveList;
//    }

    /**
     * 审批人查询自己审批过的审批表
     */
    public List<Approve> getApproverProcessedApproves(Integer approver_id) {
        return getApprove(null, null, null, null,null, null, approver_id, null, null);
    }

    /**
     * 审批人更新审批表(审批)
     */
    public boolean approveIt(Approve approve) {
        // 定义结果变量
        boolean isSuccess = true;

        // 获取审批人身份信息
        Integer approver_id = approve.getApprove_person_id();

        // 获取申请人身份信息
        Integer user_id = approve.getUser_id();

        // ========================= 检查审批表是否存在且合法或者是================================
        List<Approve> approveList = approveMapper.getOnesCurrentApproves(user_id, approve.getDocument_id());
        if (approveList.isEmpty()) {
            // 表示数据库根本不存在此表
            return false;
        }
        if (!approveList.get(0).getApprove_status().equals(0) || !approveList.get(0).getApprove_node().equals(approve.getApprove_node())) {
            // 表示此表已经被审批过
            return false;
        }

        // ========================= 检查上个节点的表是否存在且通过 ================================
        // 获取审配类型对应的流程
        List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByApproveType(approve.getApprove_type());
        // 标记上一个节点
        ApproveProcess preApproveProcess = getPreApproveProcess(approveProcessList, approve.getApprove_node());
        // 获取上一个审批表
        // TODO 假如初始节点是系统管理员，此处pre节点将不为空，需要额外添加逻辑来判断，此处&& pre节点不为 系统节点
        if (!ObjectUtils.isEmpty(preApproveProcess) && !preApproveProcess.getRole_id().equals(2)) {
            List<Approve> preApprove = approveMapper.getApprove(null, approve.getDocument_id(), approve.getApprove_type(), approve.getDocument_type(),
                    approve.getUser_id(), approve.getSystem_id(), null, preApproveProcess.getApprove_node(), 1);
            if (ObjectUtils.isEmpty(preApprove) || preApprove.isEmpty()) {
                // 上个节点的审批表不存在或没有通过
                return false;
            }
        }

        // ========================= 检查审批人是否具有相应身份和可视范围 ================================
        // 检查审批人身份是否合法
        Integer needRole_id = approveMapper.getRoleIdByApproveNodeAndType(approve.getApprove_type(), approve.getApprove_node());
//        User roleUser = userService.getRolesByUserName(userService.getUserNameById(approver_id));
//        List<Role> roleList = roleUser.getRoles();
        List<Role> roleList = approveMapper.getRolesByUserId(approver_id);
        if (!isRoleIdInList(roleList, needRole_id)) {
            // 审批人身份不合法
            return false;
        }

        // 检查审批人能否审批此表
        if (isUserInVisible(user_id, approver_id, approve.getSystem_id()) < 0) {
            // 审批人不合法
            return false;
        }
        // 标记下一个节点
        ApproveProcess nextApproveProcess;

        // ========================= 检查审批人是否具有具有特殊身份后续操作 ================================
        // 判断审批人是否为特殊身份
        if (isVeryImportantPerson(approver_id)) {
            // 重要人物下一个节点为最后节点
            nextApproveProcess = approveMapper.getLastApproveProcessByApproveType(approve.getApprove_type());
        } else {
            // 非重要人物为流程下一个节点
            nextApproveProcess = getNextApproveProcess(approveProcessList, approve.getApprove_node());
        }
        // 判断意见是否为驳回
        if (approve.getApprove_status().equals(2)){
            isSuccess = updateDocStatus(approve.getDocument_id(), 4);
        }else{
            /* 11月新加 s */
            // 判断是否是出库单
            if(approve.getDocument_type().equals(1)){
                ApproveProcess approveProcessLast = approveMapper.getLastApproveProcessByApproveType(approve.getApprove_type());
                ApproveProcess approveProcessLastPre = getPreApproveProcess(approveProcessList, approveProcessLast.getApprove_node());
                assert approveProcessLastPre != null;
                if(approve.getApprove_node().equals(approveProcessLastPre.getApprove_node())){
                    // 说明这是倒数第二个节点 此时审批完成
                    isSuccess = updateDocStatus(approve.getDocument_id(), 2);
                }
            }

            /* 11月新加 e */
            if (ObjectUtils.isEmpty(nextApproveProcess)) {
                // 代表已经完成了最后一个节点
                // 判断审批意见
                if (approve.getApprove_status().equals(1)){
                    // TODO 实现后续更新操作
                    isSuccess = updateStock(approve.getDocument_id());
                    // 判断是否是出库单
                    if(approve.getDocument_type().equals(1)){
                        /*11 月更新 doc_status = 2 -> 5*/
                        isSuccess = isSuccess && updateDocStatus(approve.getDocument_id(), 5);
                    }else{
                        isSuccess = isSuccess && updateDocStatus(approve.getDocument_id(), 2);
                    }

                }
            } else {
                if (approve.getApprove_status().equals(1)){
                    // 添加下一个审批表
                    // TODO 此处检查审批人是否具有流程连续身份,获取真正意义上的下一个节点
                    Integer nextNode = getRealNextApproveProcess(approveProcessList, approve, approver_id);
                    isSuccess = addNextLevelApprove(approve.getDocument_id(), user_id, approve.getSystem_id(), approve.getApprove_type(), approve.getDocument_type(), nextNode);
                }
            }

        }

        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String approve_time = sp.format(new Date());
        approve.setApprove_time(approve_time);
        if (ObjectUtils.isEmpty(approve.getId())){
            Integer approve_id = approveMapper.getApproveIdByNodeAndDocId(approve.getDocument_id(), approve.getApprove_node());
            approve.setId(approve_id);
        }
        return approveMapper.updateApprove(approve) && isSuccess;

    }

    public Integer getRealNextApproveProcess(List<ApproveProcess> approveProcessList, Approve approve, Integer approver_id){
        boolean start = false;
        boolean flag;
        Integer currentNode = approve.getApprove_node();
        Integer approve_type = approve.getApprove_type();
        Integer approve_system_id = approve.getSystem_id();
        for (ApproveProcess approveProcess:approveProcessList){
            if(approveProcess.getApprove_node().equals(currentNode)){
                start = true;
            }else if(start){
                flag = isApproveInVisible(approve_type,approveProcess.getApprove_node(),approve_system_id, approver_id);
                if (!flag){
                    return approveProcess.getApprove_node();
                }
            }
        }
        // 如果前面代码正确那么不可能运行至此处
        return -1;
    }



    /**
     * 判断申请人是否在审批人的可视范围 -1 代表不在范围
     */
    public Integer isUserInVisible(Integer user_id, Integer approver_id, Integer system_id){
        // 获取审批人身份信息,以及可见范围
        List<Role> approverRoles = approveMapper.getRolesByUserId(approver_id);
        Role userRole = approveMapper.getRoleByUserIdAndSysId(user_id, system_id);

        for (Role role:approverRoles){
            VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(role.getId());
            if (visibleRange.isAll()){
                return 0;
            }
            if (!system_id.equals(0)){
                if (visibleRange.isDepartment() && userRole.getDepartment_id().equals(role.getDepartment_id())){
                    return 1;
                }
                if (visibleRange.isSystem() && system_id.equals(role.getSystem_id())){
                    return 2;
                }
            }
        }
        return -1;

    }

    /**
     * 判断申请是否在审批人的可视范围
     */
    private boolean isApproveInVisible(Integer approve_type, Integer approve_node, Integer approve_system_id, Integer approver_id){
        Integer needRoleId = approveMapper.getRoleIdByApproveNodeAndType(approve_type, approve_node);
        List<Role> approverRoleList = approveMapper.getRolesByUserId(approver_id);

        // Role userRole = approveMapper.getRoleByUserIdAndSysId(user_id, system_id);
        // Integer department_id = userRole.getDepartment_id();
        Integer userDepartmentId = approveMapper.getDepartmentIdBySysId(approve_system_id);

        if (!isRoleIdInList(approverRoleList, needRoleId)) {
            // 审批人身份不合法
            return false;
        }
        // 检查审批人能否审批此表
        Integer index = isRoleIdInListInt(approverRoleList, needRoleId);
        Role approverNeedRole = approverRoleList.get(index);
        VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(needRoleId);
        if (visibleRange.isAll()){
            return true;
        }
        if (visibleRange.isDepartment() && userDepartmentId.equals(approverNeedRole.getDepartment_id())){
            return true;
        }
        return visibleRange.isSystem() && approve_system_id.equals(approverNeedRole.getSystem_id());
    }

    /**
     * 判断身份表内是否包含某个role_id
     */
    private boolean isRoleIdInList(List<Role> roleList, Integer role_id) {
        for (Role role : roleList) {
            if (role_id.equals(role.getId())) {
                return true;
            }
        }
        return false;
    }

    private Integer isRoleIdInListInt(List<Role> roleList, Integer role_id) {
        int index = 0;
        for (Role role : roleList) {
            if (role_id.equals(role.getId())) {
                return index;
            }
            index ++;
        }
        return -1;
    }

    /**
     * 根据流程获取下一个节点
     */
    private ApproveProcess getNextApproveProcess(List<ApproveProcess> approveProcessList, Integer currentNode) {
        if (approveProcessList.isEmpty() || currentNode.equals((approveProcessList.get(approveProcessList.size() - 1)).getApprove_node())) {
            // 如果为最后一个节点
            return null;
        }
        for (int i = 0; i < approveProcessList.size() - 1; i++) {
            if (currentNode.equals(approveProcessList.get(i).getApprove_node())) {
                return approveProcessList.get(i + 1);
            }
        }
        return null;
    }

    /**
     * 根据流程获取上一个节点
     */
    private ApproveProcess getPreApproveProcess(List<ApproveProcess> approveProcessList, Integer currentNode) {
        if (approveProcessList.isEmpty() || currentNode.equals((approveProcessList.get(0).getApprove_node()))) {
            // 如果为第一个节点
            return null;
        }
        for (int i = 1; i < approveProcessList.size(); i++) {
            if (currentNode.equals(approveProcessList.get(i).getApprove_node())) {
                return approveProcessList.get(i - 1);
            }
        }
        return null;
    }

    /**
     * 根据流程获取当前节点
     */
    private ApproveProcess getCurrentApproveProcess(List<ApproveProcess> approveProcessList, Integer currentNode) {
        if (approveProcessList.isEmpty()) {
            // 如果为空
            return null;
        }
        for (ApproveProcess approveProcess : approveProcessList) {
            if (currentNode.equals(approveProcess.getApprove_node())) {
                return approveProcess;
            }
        }
        return null;
    }


    /**
     * 判断审批人是否为特殊身份人员
     */
    public boolean isVeryImportantPerson(Integer approver_id) {
        Integer id = approveMapper.getVeryImportantPerson(approver_id);
        return !ObjectUtils.isEmpty(id);
    }

    /**
     * 更新库存（自动判断）
     * 1:领用单
     * 2:借用单
     * 3:入库单
     * 4:归还单
     * 5:报废单
     * 6:报检单
     * 7:报检报修返回单
     * */
    public boolean updateStock(String document_id) {
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        boolean isSuccess1;
        boolean isSuccess2;
        boolean isSuccess;
        // 在数据库内部查询单据类型
        Integer docType= getDocumentType(document_id);
        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            isSuccess = Boolean.TRUE;
            for (DocumentDevice documentDevice : documentDeviceList) {
                String device_id = documentDevice.getDevice_id();
                Integer deviceNumber = documentDevice.getDevice_number();
                if(docType.equals(1)){
                    isSuccess1 = deviceService.updateAddOutwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(2)){
                    isSuccess1 = deviceService.updateAddLendNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(3)){
                    List<Device> devices = deviceService.getDeviceTempList(null, device_id, null,
                            null,null, document_id);
                    if(ObjectUtils.isEmpty(devices)){return false;}
                    Device device = devices.get(0);
                    isSuccess1 = deviceService.translateDeviceTempToDevice(device_id, document_id);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
//                    isSuccess2 = true;
                }else if(docType.equals(4)){
                    isSuccess1 = deviceService.updateAddReturnNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(5)){
                    isSuccess1 = deviceService.updateAddScrapNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(6)){
                    isSuccess1 = deviceService.updateAddRepairwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else{
                    isSuccess1 = deviceService.updateAddRestockWarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }

                isSuccess = isSuccess1 && isSuccess2;
                if(!isSuccess){break;}
            }
        } else {
            isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }

    /**
     * 更新库存，手动指定类型
     * */
    public boolean updateStockNoAuto(String document_id, Integer docType) {
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        boolean isSuccess1;
        boolean isSuccess2;
        boolean isSuccess;
        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            isSuccess = Boolean.TRUE;
            for (DocumentDevice documentDevice : documentDeviceList) {
                String device_id = documentDevice.getDevice_id();
                Integer deviceNumber = documentDevice.getDevice_number();
                if(docType.equals(1)){
                    isSuccess1 = deviceService.updateAddOutwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(2)){
                    isSuccess1 = deviceService.updateAddLendNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(3)){
                    isSuccess1 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                    isSuccess2 = true;
                }else if(docType.equals(4)){
                    isSuccess1 = deviceService.updateAddReturnNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(5)){
                    isSuccess1 = deviceService.updateAddScrapNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(6)){
                    isSuccess1 = deviceService.updateAddRepairwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else{
                    isSuccess1 = deviceService.updateAddRestockWarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }
                isSuccess = isSuccess1 && isSuccess2;
                if(!isSuccess){break;}
            }
        } else {
            isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }


    /**
     * 回退数据库更新，自动判断类型
     * */
    public boolean fallback(String document_id){
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        boolean isSuccess1;
        boolean isSuccess2;
        boolean isSuccess;
        // 在数据库内部查询单据类型
        Integer docType= getDocumentType(document_id);
        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            isSuccess = Boolean.TRUE;
            for (DocumentDevice documentDevice : documentDeviceList) {
                String device_id = documentDevice.getDevice_id();
                Integer deviceNumber = documentDevice.getDevice_number();
                if(docType.equals(1)){
                    isSuccess1 = deviceService.updateMinusOutwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(2)){
                    isSuccess1 = deviceService.updateMinusLendNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(3)){
                    isSuccess1 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                    isSuccess2 = true;
                }else if(docType.equals(4)){
                    isSuccess1 = deviceService.updateMinusReturnNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(5)){
                    isSuccess1 = deviceService.updateMinusScrapNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(6)){
                    isSuccess1 = deviceService.updateMinusRepairwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else{
                    isSuccess1 = deviceService.updateMinusRestockWarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }
                isSuccess = isSuccess1 && isSuccess2;
                if(!isSuccess){break;}
            }
        } else {
            isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }

    /**
     * 回退数据库更新，手动指定类型
     * */
    public boolean fallbackNoAuto(String document_id, Integer docType){
        List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(document_id);
        boolean isSuccess1;
        boolean isSuccess2;
        boolean isSuccess;
        if (documentDeviceList != null && !documentDeviceList.isEmpty()) {
            isSuccess = Boolean.TRUE;
            for (DocumentDevice documentDevice : documentDeviceList) {
                String device_id = documentDevice.getDevice_id();
                Integer deviceNumber = documentDevice.getDevice_number();
                if(docType.equals(1)){
                    isSuccess1 = deviceService.updateMinusOutwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(2)){
                    isSuccess1 = deviceService.updateMinusLendNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(3)){
                    isSuccess1 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                    isSuccess2 = true;
                }else if(docType.equals(4)){
                    isSuccess1 = deviceService.updateMinusReturnNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(5)){
                    isSuccess1 = deviceService.updateMinusScrapNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else if(docType.equals(6)){
                    isSuccess1 = deviceService.updateMinusRepairwarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateInventoryNumber(device_id, deviceNumber);
                }else{
                    isSuccess1 = deviceService.updateMinusRestockWarehouseNumber(device_id, deviceNumber);
                    isSuccess2 = deviceService.updateMinusInventoryNumber(device_id, deviceNumber);
                }
                isSuccess = isSuccess1 && isSuccess2;
                if(!isSuccess){break;}
            }
        } else {
            isSuccess = Boolean.FALSE;
        }
        return isSuccess;
    }



    /** 判断单据类型 1为领用 2为借出 3为入库*/
    public Integer getDocumentType(String document_id){
        List<Inout> inoutList = inoutService.getOutListParams(document_id, null, null, null);
        if(! ObjectUtils.isEmpty(inoutList) && !inoutList.isEmpty()){
            Inout inout = inoutList.get(0);
            if(inout.getDocument_category_id().equals(0)){
                // 入库单
                return 3;
            }else{
                // 出库单
                return 1;
            }
        }
        List<Lend> lendList = lendService.getLendListByDocId(document_id);
        if(! ObjectUtils.isEmpty(lendList) && ! lendList.isEmpty()){
            return 2;
        }
        List<Return> returnList = returnService.getReturnList(document_id, null, null);
        if(! ObjectUtils.isEmpty(returnList) && ! returnList.isEmpty()){
            return 4;
        }

        List<Scrap> scrapList = scrapService.getScrapListParams(document_id, null);
        if(! ObjectUtils.isEmpty(scrapList) && ! scrapList.isEmpty()){
            return 5;
        }

        List<Repair> repairList = repairService.getRepairListParams(document_id, null, null);
        if(! ObjectUtils.isEmpty(repairList) && ! repairList.isEmpty()){
            return 6;
        }
        List<Restock> restockList = restockService.getRestockListParams(null, document_id, null, null,null);
        if(! ObjectUtils.isEmpty(restockList) && ! restockList.isEmpty()){
            return 7;
        }

        return 0;
    }
    /** 根据审批类型获取审批流程第一个节点 */
    public ApproveProcess getFirstApproveProcessByApproveType(Integer approve_type){
        return approveMapper.getFirstApproveProcessByApproveType(approve_type);
    }

    /** 根据用户id和审批类型获取下一个节点*/
    public ApproveProcess getNextApproveProcessByUserIdAndApproveType(Integer user_id, Integer approve_type, Integer system_id){
        //User roleUser = userService.getRolesByUserName(userService.getUserNameById(user_id));
//        if(ObjectUtils.isEmpty(user_id)){
//            // 没有用户id说明是入库单，默认获取第一个节点
//            return getFirstApproveProcessByApproveType(approve_type);
//        }
        List<Role> roleList = getRolesByUserId(user_id);
        List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByApproveType(approve_type);
        Role realRole = null;
        for (Role role:roleList){
            VisibleRange visibleRange = approveMapper.getVisibleRangeByRoleId(role.getId());
            if(ObjectUtils.isEmpty(visibleRange)){
                break;
            }
            if(visibleRange.isSystem() && system_id.equals(role.getSystem_id())){
                realRole = role;
                break;
            }
        }
        if (ObjectUtils.isEmpty(realRole)){
            return getFirstApproveProcessByApproveType(approve_type);
        }
        for (int i=0;i<approveProcessList.size();i++){
            if (realRole.getId().equals(approveMapper.getRoleIdByApproveNodeAndType(approve_type, approveProcessList.get(i).getApprove_node()))){
                if(i != approveProcessList.size()-1){
                    return approveProcessList.get(i+1);
                }
            }
        }
        return null;
    }

    /** 审批人根据document_id和审批人信息查询审批链条 */
    public List<Approve> getApproveChainByDocIdAndApproverId(String document_id, Integer approver_id){
        List<Approve> latestApproveList = getOnesPostCurrentApproves(null, document_id);
        if(ObjectUtils.isEmpty(latestApproveList) || latestApproveList.isEmpty()){
            return null;
        }
        Approve latestApprove = latestApproveList.get(0);
        Integer approve_type = latestApprove.getApprove_type();
        // TODO 如果审批单的系统id与审批人的系统不一致或者审批人系统id为0，这里需要改进！！！
        ApproveProcess approveProcess = getApproveProcessByApproverIdAndApproveType(approver_id, approve_type, latestApprove.getSystem_id());
        if (ObjectUtils.isEmpty(approveProcess)){
            return null;
        }
        if(latestApprove.getApprove_node() < approveProcess.getApprove_node()){
            return null;
        }
        List<Approve> approveList = getApprove(null, document_id, null, null, null,
                null, null, null, null);
        List<Approve> res = new ArrayList<>();
        for(Approve approve:approveList){
            if (approve.getApprove_node() <= approveProcess.getApprove_node()) {
                res.add(approve);
            }
        }
        return res;

    }

    /** 根据审批人以及审批类型获得审批接节点 */
    public ApproveProcess getApproveProcessByApproverIdAndApproveType(Integer approver_id, Integer approve_type, Integer system_id){
        Role role = getRoleByUserIdAndSysId(approver_id, system_id);
        if(ObjectUtils.isEmpty(role)){
            role = getRoleByUserIdAndSysId(approver_id, 0);
        }
        List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByApproveType(approve_type);
        for (ApproveProcess approveProcess:approveProcessList){
            if (approveProcess.getRole_id().equals(role.getId())){
                return approveProcess;
            }
        }
        return null;
    }

    /** 根据user_id和system_id获取角色 */
    public Role getRoleByUserIdAndSysId(Integer user_id, Integer system_id){
        if (ObjectUtils.isEmpty(system_id ) || ObjectUtils.isEmpty(user_id)){
            return null;
        }
        return approveMapper.getRoleByUserIdAndSysId(user_id, system_id);
    }

    /** 为审批表添加人员信息 */
    public List<Approve> addInfoForApproves(List<Approve> approveList){
        approveList.forEach(approve -> {
            String user_name = userService.getDisplayNameById(approve.getUser_id());
            String approver_name = userService.getDisplayNameById(approve.getApprove_person_id());
            approve.setUser_name(user_name);
            approve.setApprover_name(approver_name);
            String system_name = approveMapper.getSysNameBySysId(approve.getSystem_id());
            approve.setSystem_name(system_name);
        });
        return approveList;
    }

    /** 为审批表添加单据信息 */
    public List<Approve> addDocInfoForApproves(List<Approve> approveList){
        for(Approve approve:approveList){
            if (approve.getDocument_type().equals(1)){
                List<Inout> inoutList = inoutService.getOutListParams(approve.getDocument_id(), null, null, null);
                if (ObjectUtils.isEmpty(inoutList)){
                    continue;
                }
                approve.setDoc(inoutList.get(0));
            }else if(approve.getDocument_type().equals(2)){
                List<Lend> lendList = lendService.getLendList(approve.getDocument_id(), null, null);
                if (ObjectUtils.isEmpty(lendList)){
                    continue;
                }
                approve.setDoc(lendList.get(0));
            }else if(approve.getDocument_type().equals(3)){
                List<Inout> inoutList = inoutService.getInListParams(approve.getDocument_id(), null, null, null);
                if (ObjectUtils.isEmpty(inoutList)){
                    continue;
                }
                approve.setDoc(inoutList.get(0));
            }else if(approve.getDocument_type().equals(5)) {
                List<Scrap> scrapList = scrapService.getScrapListParams(approve.getDocument_id(), null);
                if (ObjectUtils.isEmpty(scrapList)) {
                    continue;
                }
                approve.setDoc(scrapList.get(0));
            }else if(approve.getDocument_type().equals(6)){
                List<Repair> repairList = repairService.getRepairListParams(approve.getDocument_id(), null,null);
                if (ObjectUtils.isEmpty(repairList)) {
                    continue;
                }
                approve.setDoc(repairList.get(0));
            }else if(approve.getDocument_type().equals(7)){
                List<Restock> restockList = restockService.getRestockListParams(null, approve.getDocument_id(),
                        null,null, null);
                if (ObjectUtils.isEmpty(restockList)) {
                    continue;
                }
                approve.setDoc(restockList.get(0));
            }else {
                return approveList;
            }
        }
        return approveList;
    }

    /** 根据user_id 获取roles */
    public List<Role> getRolesByUserId(Integer user_id){
        return approveMapper.getRolesByUserId(user_id);
    }

    /** 查询文件状态描述 */
    public String getStatusDes(Integer document_status){
        return approveMapper.getStatusDes(document_status);
    }

    /** 更新单据状态 无限制*/
    public boolean updateDocStatus(String document_id, Integer docStatus){
        Integer docType= getDocumentType(document_id);
        if (docType.equals(1)){
            List<Inout> inoutList = inoutService.getOutListParams(document_id, null, null, null);
            if (ObjectUtils.isEmpty(inoutList)){
                return false;
            }
            Inout inout = inoutList.get(0);
            inout.setDocument_status(docStatus);
            return inoutService.updateOut(inout);
        }else if (docType.equals(2)){
            List<Lend> lendList = lendService.getLendList(document_id, null, null);
            if (ObjectUtils.isEmpty(lendList)){
                return false;
            }
            Lend lend = lendList.get(0);
            lend.setDocument_status(docStatus);
            return lendService.updateLend(lend);
        }else if (docType.equals(3)){
            List<Inout> inoutList = inoutService.getInListParams(document_id, null, null, null);
            if (ObjectUtils.isEmpty(inoutList)){
                return false;
            }
            Inout inout = inoutList.get(0);
            inout.setDocument_status(docStatus);
            return inoutService.updateIn(inout);
        }else if (docType.equals(5)){
            List<Scrap> scrapList = scrapService.getScrapListParams(document_id, null);
            if (ObjectUtils.isEmpty(scrapList)){
                return false;
            }
            Scrap scrap = scrapList.get(0);
            scrap.setDocument_status(docStatus);
            return scrapService.updateScrap(scrap);
        }else if (docType.equals(6)){
            List<Repair> repairList = repairService.getRepairListParams(document_id,null,null);
            if (ObjectUtils.isEmpty(repairList)){
                return false;
            }
            Repair repair = repairList.get(0);
            repair.setDocument_status(docStatus);
            return repairService.updateRepair(repair);
        }else if (docType.equals(7)){
            List<Restock> restockList = restockService.getRestockListParams(null,document_id,null,
                    null,null);
            if (ObjectUtils.isEmpty(restockList)){
                return false;
            }
            Restock restock = restockList.get(0);
            restock.setDocument_status(docStatus);
            return restockService.updateRestock(restock).getIsSuccess();
        }else {
            return false;
        }
    }

    public List<Role> getSysDepartInfoByDocId(String document_id, Integer doc_status, Integer docType, String displayName){
        if (docType.equals(3)){
            // 入库单
            doc_status = 2;
        }else{
            if(doc_status.equals(0)){
                // 编辑状态不可查
                return null;
            }
        }

        Integer sys_id = null;
        Integer department_id = null;
        String sys_name = null;
        String department_name = null;
        Role role = new Role();
        List<Role> roleList = new ArrayList<>();
        if (docType.equals(1) || docType.equals(2)){
            List<Approve> approveList = approveMapper.getApprove(null, document_id, null, null, null, null,
                    null, null, null);
            if(ObjectUtils.isEmpty(approveList)){
                return null;
            }
            sys_id = approveList.get(0).getSystem_id();
            department_id = approveMapper.getDepartmentIdBySysId(sys_id);
            sys_name = approveMapper.getSysNameBySysId(sys_id);
            department_name = approveMapper.getDepartmentNameByDepartmentId(department_id);

            role.setSystem_id(sys_id);
            role.setDepartment_id(department_id);
            role.setSystem_name(sys_name);
            role.setDepartment_name(department_name);
            assert false;
            roleList.add(role);
        }else if(docType.equals(3)){
            Integer user_id = userService.getUserIdByDisplayName(displayName);
            roleList = getRolesByUserId(user_id);
        }else{
            return null;
        }

        return roleList;
    }

    // 判断是否还有库存的核心方法
    public List<DocumentDevice> getAllOnApprovingDevices(List<DocumentDevice> onApprovingDevicesList, Integer docType){
        Map<String, Integer> map = new HashMap<>();
        // 此处需要对参数进行深拷贝
        List<DocumentDevice> onApprovingDevicesListCopy = new ArrayList<>();
        for(DocumentDevice documentDevice:onApprovingDevicesList){
            Device device = deviceService.getDeviceInfoByDeviceId(documentDevice.getDevice_id());
            DocumentDevice documentDevice1 = new DocumentDevice();
            documentDevice1.setDevice_name(device.getName());
            documentDevice1.setDevice_rest_number(device.getInventory_number());
            documentDevice1.setDevice_id(documentDevice.getDevice_id());
            documentDevice1.setDevice_number(documentDevice.getDevice_number());
//            documentDevice.setDevice_name(device.getName());
//            documentDevice.setDevice_rest_number(device.getInventory_number());
            map.put(documentDevice.getDevice_id(), 0);
            onApprovingDevicesListCopy.add(documentDevice1);
        }

        // 领用单
        List<Inout> inoutList = inoutService.getOutListParams(null, null, null, 1);
        for(Inout inout:inoutList){
            List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(inout.getDocument_id());
            for(DocumentDevice documentDevice:documentDeviceList){
                if(map.containsKey(documentDevice.getDevice_id())){
                    map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                }
            }
        }
        // 借用单
        List<Lend> lendList = lendService.getLendList(null, null, 1);
        for(Lend lend:lendList){
            List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(lend.getDocument_id());
            for(DocumentDevice documentDevice:documentDeviceList){
                if(map.containsKey(documentDevice.getDevice_id())){
                    map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                }
            }
        }

        // 报废
        List<Scrap> scrapList = scrapService.getScrapListParams(null, 1);
        for(Scrap scrap:scrapList){
            List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(scrap.getDocument_id());
            for(DocumentDevice documentDevice:documentDeviceList){
                if(map.containsKey(documentDevice.getDevice_id())){
                    map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                }
            }
        }

        // 报检报修
        List<Repair> repairList = repairService.getRepairListParams(null, null, 1);
        for(Repair repair:repairList){
            List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(repair.getDocument_id());
            for(DocumentDevice documentDevice:documentDeviceList){
                if(map.containsKey(documentDevice.getDevice_id())){
                    map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                }
            }
        }

        // 报检报修归还
        List<Restock> restockList = restockService.getRestockListParams(null, null, null, null, 1);
        for(Restock restock:restockList){
            List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(restock.getDocument_id());
            for(DocumentDevice documentDevice:documentDeviceList){
                if(map.containsKey(documentDevice.getDevice_id())){
                    map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                }
            }
        }

        for(DocumentDevice documentDevice:onApprovingDevicesListCopy){
            Integer onApprovingNum = map.get(documentDevice.getDevice_id());

            // 设置是否库存充足
            documentDevice.setEnough(onApprovingNum + documentDevice.getDevice_number() <= documentDevice.getDevice_rest_number());
            documentDevice.setDevice_number(onApprovingNum);
        }
        return onApprovingDevicesListCopy;
    }

    // 判断库存是否充足
    public Result isEnough(Integer docType, Inout inout, Lend lend){
        Result result = new Result();
        boolean isSuccess = true;
        List<DocumentDevice> documentDeviceList = null;
        if(docType.equals(1) && !ObjectUtils.isEmpty(inout)){
            documentDeviceList = inout.getDocumentDeviceList();
        }else if(docType.equals(2) && !ObjectUtils.isEmpty(lend)){
            documentDeviceList = lend.getDocumentDeviceList();
        }else {
            result.setMsg("系统内部调用错误！");
            result.setCode(-1);
            result.setIsSuccess(false);
            return result;
        }
        List<DocumentDevice> onApprovingDevicesList = null;
        if(!ObjectUtils.isEmpty(documentDeviceList)){
            onApprovingDevicesList = getAllOnApprovingDevices(documentDeviceList, 2);
        }
        // 判断标记
        if(!ObjectUtils.isEmpty(onApprovingDevicesList)){
            StringBuilder msg = new StringBuilder("库存余量不足！具体情况：\n");
            for (DocumentDevice documentDevice:onApprovingDevicesList){
                if(! documentDevice.isEnough()){
                    isSuccess = false;
                    msg.append("设备名：").append(documentDevice.getDevice_name()).append("  库存余量：").append(documentDevice.getDevice_rest_number()).append("  正在审批数量：").append(documentDevice.getDevice_number()).append("\n");
                }
            }
            if(!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg(msg.toString());
                result.setList(onApprovingDevicesList);
                return result;
            }
        }
        result.setIsSuccess(true);
        return result;
    }

    public Result isEnough(Integer docType, DeviceDocument doc ){
        Result result = new Result();
        boolean isSuccess = true;
        List<DocumentDevice> documentDeviceList = doc.getDocumentDeviceList();
        List<DocumentDevice> onApprovingDevicesList = null;
        if(!ObjectUtils.isEmpty(documentDeviceList)){
            onApprovingDevicesList = getAllOnApprovingDevices(documentDeviceList, docType);
        }
        // 判断标记
        if(!ObjectUtils.isEmpty(onApprovingDevicesList)){
            StringBuilder msg = new StringBuilder("库存余量不足！具体情况：\n");
            for (DocumentDevice documentDevice:onApprovingDevicesList){
                if(! documentDevice.isEnough()){
                    isSuccess = false;
                    msg.append("设备名：").append(documentDevice.getDevice_name()).append("  库存余量：").append(documentDevice.getDevice_rest_number()).append("  正在审批数量：").append(documentDevice.getDevice_number()).append("\n");
                }
            }
            if(!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg(msg.toString());
                result.setList(onApprovingDevicesList);
                return result;
            }
        }
        result.setIsSuccess(true);
        return result;
    }

    public Result getApproversByApprove(Approve approve){
        Result result = new Result();

        // 获取审配类型对应的流程
        List<ApproveProcess> approveProcessList = approveMapper.getApproveProcessByApproveType(approve.getApprove_type());
        if(ObjectUtils.isEmpty(approveProcessList) || approveProcessList.size() <= 0){
            result.setIsSuccess(false);
            result.setCode(-1);
            result.setMsg("审批流程类型异常！");
            return result;
        }

        // 获取前序、当前、目标节点
        ApproveProcess preApproveProcess = getPreApproveProcess(approveProcessList, approve.getApprove_node());
        ApproveProcess approveProcess = getCurrentApproveProcess(approveProcessList, approve.getApprove_node());
        ApproveProcess nextApproveProcess = null;


        // 判断前序节点是否为重要人物,并得到真正的目标节点
        if (!ObjectUtils.isEmpty(preApproveProcess) && !preApproveProcess.getRole_id().equals(2)) {
            List<Approve> preApprove = approveMapper.getApprove(null, approve.getDocument_id(), approve.getApprove_type(), approve.getDocument_type(),
                    approve.getUser_id(), approve.getSystem_id(), null, preApproveProcess.getApprove_node(), 1);
            if (ObjectUtils.isEmpty(preApprove) || preApprove.isEmpty()) {
                // 上个节点的审批表不存在或没有通过
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg("审批流程异常！上个审批没有完成或没通过");
                return result;
            }
            if(isVeryImportantPerson(preApprove.get(0).getApprove_person_id())){
                // 上个节点是重要人物审批
                nextApproveProcess = approveMapper.getLastApproveProcessByApproveType(approve.getApprove_type());
            }else{
                // 不是重要人物审批
                nextApproveProcess = approveProcess;
            }
        }else{
            nextApproveProcess = approveProcess;
        }

        // 获取目标节点需要的身份信息和系统部门信息
        Integer needRoleId = nextApproveProcess.getRole_id();
        Integer systemId = approve.getSystem_id();
        Integer departmentId = null;
        if (! systemId.equals(0)){
            departmentId = approveMapper.getDepartmentIdBySysId(systemId);
        }else{
            departmentId = 0;
        }
        List<Integer> user_ids = approveMapper.getUserIdsParams(needRoleId, systemId, departmentId);
        if(ObjectUtils.isEmpty(user_ids)){
            user_ids.addAll(approveMapper.getUserIdsParams(needRoleId, null, departmentId));
        }
        //user_ids.addAll(approveMapper.getUserIdsParams(needRoleId, systemId, null));
        //user_ids.addAll(approveMapper.getUserIdsParams(needRoleId, null, departmentId));
        if (user_ids.isEmpty()){
            user_ids.addAll(approveMapper.getUserIdsParams(needRoleId, 0, 0));
        }
        Set<String> userSet = new HashSet<>();
        for(Integer user_id:user_ids){
            String displayName = userService.getDisplayNameById(user_id);
            userSet.add(displayName);
        }
        result.setList(Arrays.asList(userSet.toArray()));
        result.setCode(userSet.size());
        result.setIsSuccess(true);
        result.setMsg("获取审批人成功！");
        return result;
    }

    public Result isBeyondForLendDoc(Integer docType, DeviceDocument doc, String lend_document_id){
        Result result = new Result();
        boolean isSuccess = true;
        List<DocumentDevice> documentDeviceList = doc.getDocumentDeviceList();
        List<DocumentDevice> onApprovingDevicesList = null;
        if(!ObjectUtils.isEmpty(documentDeviceList)){
            onApprovingDevicesList = getAllOnApprovingDevicesForLendDoc(documentDeviceList, docType, lend_document_id);
        }
        // 判断标记
        if(!ObjectUtils.isEmpty(onApprovingDevicesList)){
            StringBuilder msg = new StringBuilder("归还设备超出原单据记录！具体情况：\n");
            for (DocumentDevice documentDevice:onApprovingDevicesList){
                if(! documentDevice.isEnough()){
                    isSuccess = false;
                    msg.append("设备名：").append(documentDevice.getDevice_name()).append("  单据数量：").append(documentDevice.getDevice_rest_number()).append("  正在审批数量：").append(documentDevice.getDevice_number()).append("\n");
                }
            }
            if(!isSuccess){
                result.setIsSuccess(false);
                result.setCode(-1);
                result.setMsg(msg.toString());
                result.setList(onApprovingDevicesList);
                return result;
            }
        }
        result.setIsSuccess(true);
        return result;
    }

    public List<DocumentDevice> getAllOnApprovingDevicesForLendDoc(List<DocumentDevice> onApprovingDevicesList,
                                                                   Integer docType, String lend_document_id){
        Map<String, Integer> map = new HashMap<>();
        List<DocumentDevice> documentDevices = null;

        documentDevices = documentDeviceService.getDocumentDeviceInfo(lend_document_id);
        // 此处需要对参数进行深拷贝
        List<DocumentDevice> onApprovingDevicesListCopy = new ArrayList<>();
        for(DocumentDevice documentDevice:onApprovingDevicesList){
            Device device = deviceService.getDeviceInfoByDeviceId(documentDevice.getDevice_id());
            DocumentDevice documentDevice1 = new DocumentDevice();
            documentDevice1.setDevice_name(device.getName());
            documentDevice1.setDevice_rest_number(getDeviceNumFromList(documentDevices, device));
            documentDevice1.setDevice_id(documentDevice.getDevice_id());
            documentDevice1.setDevice_number(documentDevice.getDevice_number());
//            documentDevice.setDevice_name(device.getName());
//            documentDevice.setDevice_rest_number(device.getInventory_number());
            map.put(documentDevice.getDevice_id(), 0);
            onApprovingDevicesListCopy.add(documentDevice1);
        }
        if (docType.equals(4)){
            // 归还
            List<Return> returnList = returnService.getReturnList(lend_document_id, null, null);
            for(Return ret:returnList){
                List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(ret.getDocument_id());
                for(DocumentDevice documentDevice:documentDeviceList){
                    if(map.containsKey(documentDevice.getDevice_id())){
                        map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                    }
                }
            }
        }else if (docType.equals(7)){
            // 报检报修归还
            List<Restock> restockList = restockService.getRestockListParams(null, null, lend_document_id, null, 1);
            restockList.addAll(restockService.getRestockListParams(null, null, lend_document_id, null, 2));
            for(Restock restock:restockList){
                List<DocumentDevice> documentDeviceList = documentDeviceService.getDocumentDeviceInfo(restock.getDocument_id());
                for(DocumentDevice documentDevice:documentDeviceList){
                    if(map.containsKey(documentDevice.getDevice_id())){
                        map.compute(documentDevice.getDevice_id(), (key, value) -> ObjectUtils.isEmpty(value)? value = 0:value + documentDevice.getDevice_number());
                    }
                }
            }
        }

        for(DocumentDevice documentDevice:onApprovingDevicesListCopy){
            Integer onApprovingNum = map.get(documentDevice.getDevice_id());

            // 设置是否库存充足
            documentDevice.setEnough(onApprovingNum + documentDevice.getDevice_number() <=documentDevice.getDevice_rest_number() );
            documentDevice.setDevice_number(onApprovingNum);
        }
        return onApprovingDevicesListCopy;
    }

    private Integer getDeviceNumFromList(List<DocumentDevice> documentDeviceList, Device device){
        for(DocumentDevice documentDevice:documentDeviceList){
            if(documentDevice.getDevice_id().equals(device.getDevice_id())){
                return documentDevice.getDevice_number();
            }
        }
        return 0;
    }

//    /** 库管员确认单据 */
//    public boolean confirmApprove(Approve approve){
//        ApproveProcess approveProcess =  approveMapper.getLastApproveProcessByApproveType(approve.getApprove_type());
//        if(!approve.getApprove_node().equals(approveProcess.getApprove_node())){
//            // 审批表不是最后一个节点
//            return false;
//        }
//    }
}

  