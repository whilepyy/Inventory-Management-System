package com.example.dakudemo.service;

/*
 * @author:chh
 * @Date:2022-09-04-11:14
 * @Description:
 */

import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Restock;
import com.example.dakudemo.entity.Result;
import com.example.dakudemo.mapper.RestockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Service
public class RestockServiceImpl implements RestockService{
    @Autowired
    private RestockMapper restockMapper;

    @Autowired
    private DeviceDocumentService deviceDocumentService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private DocumentDeviceService documentDeviceService;

    public Result addRestock(Restock restock) {
        Result result = new Result();
        Boolean isSuccess = true;
        List<Restock> restockList = restockMapper.getRestockListParams(null, restock.getDocument_id(),
                null, null, restock.getDocument_status());
        if(!restockList.isEmpty()){
            result.setMsg("单据已经存在！");
            result.setIsSuccess(false);
            result.setCode(-1);
            return result;
        }

        isSuccess = restockMapper.addRestock(restock);
        result.setIsSuccess(isSuccess);
        return result;
    }

    public Result deleteRestock(Integer id) {
        Result result = new Result();
        List<Restock> restockList = restockMapper.getRestockListParams(id, null,
                null, null, null);
        if(restockList.isEmpty()){
            result.setMsg("单据不存在！");
            result.setIsSuccess(false);
            result.setCode(-1);
            return result;
        }
        Boolean isSuccess = restockMapper.deleteRestock(id) && approveService.fallbackNoAuto(restockList.get(0).getDocument_id(), 7);
        result.setIsSuccess(isSuccess);
        return result;
    }

    public Result updateRestock(Restock restock) {
        Result result = new Result();
        List<Restock> restockList = restockMapper.getRestockListParams(null, restock.getDocument_id(),
                null, null, null);
        if(restockList.isEmpty()){
            result.setMsg("单据不存在！");
            result.setIsSuccess(false);
            result.setCode(-1);
            return result;
        }
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(restock.getDocument_id());
        for(DocumentDevice documentDevice: restock.getDocumentDeviceList()){
            if(!restock.getDocument_id().equals(documentDevice.getDocument_id())){
                result.setMsg("单据存在问题：单据id对应错误！");
                result.setIsSuccess(false);
                result.setCode(-1);
                return result;
            }
            isSuccess = documentDeviceService.addDocumentDevice(documentDevice);
            if (!isSuccess){
                result.setMsg("添加设备单据信息错误！");
                result.setIsSuccess(false);
                result.setCode(-1);
                return result;
            }
        }
        isSuccess = isSuccess && restockMapper.updateRestock(restock);
        result.setIsSuccess(isSuccess);
        return result;
    }

    public List<Restock> getRestockListParams(Integer id, String document_id, String check_repair_document_id, Integer document_person_id, Integer document_status) {
        List<Restock> restockList = restockMapper.getRestockListParams(id, document_id, check_repair_document_id, document_person_id, document_status);
        restockList = deviceDocumentService.setDevicesAndDocumentDevice(restockList);
        return restockList;
    }

    public List<Restock> getRestockList() {
        List<Restock> restockList = restockMapper.getRestockList();
        restockList = deviceDocumentService.setDevicesAndDocumentDevice(restockList);
        return restockList;
    }
}
