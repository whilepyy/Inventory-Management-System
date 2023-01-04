package com.example.dakudemo.service;

import com.example.dakudemo.entity.Scrap;
import com.example.dakudemo.mapper.ScrapApprovalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/2 9:58
 */
@Service
public class ScrapApprovalImpl implements ScrapApprovalService{
    @Autowired
    private ScrapApprovalMapper scrapApprovalMapper;

    @Autowired
    private DeviceDocumentService deviceDocumentService;


    /**删除审批*/
    public Boolean deleteScrapApproval(Integer id){
        return scrapApprovalMapper.deleteScrapApproval(id);
    }

    /**修改审批状态*/
    public Boolean updateScrapApproval(Integer id, String approve_status, String approve_time){
        return scrapApprovalMapper.updateScrapApproval(id, approve_status, approve_time);
    }

    /**查询报废审批列表*/
    public List<Scrap> getScrapApprovalList(String approve_status, Integer approve_person_id){
        List<Scrap> scrapList = scrapApprovalMapper.getScrapApprovalList(approve_status, approve_person_id);
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }
    public List<Scrap> getScrapApprovalListAll(String approve_status){
        List<Scrap> scrapList = scrapApprovalMapper.getScrapApprovalListAll(approve_status);
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }


    /**通过id获取document_id*/
    public String  getDocumentIdById(Integer id){
        return scrapApprovalMapper.getDocumentIdById(id);
    }
}
