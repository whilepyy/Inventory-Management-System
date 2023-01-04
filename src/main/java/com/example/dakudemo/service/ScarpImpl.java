package com.example.dakudemo.service;

import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Scrap;
import com.example.dakudemo.mapper.ScrapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/2 9:49
 */
@Service
public class ScarpImpl implements ScrapService{

    @Autowired
    private ScrapMapper scrapMapper;

    @Autowired
    private DeviceDocumentService deviceDocumentService;

    @Autowired
    private DocumentDeviceService documentDeviceService;

    /**增加报废单**/
    public boolean addScrap(Scrap scrap){
        return scrapMapper.addScrap(scrap);
    }

    /**删除报废单**/
    public boolean deleteScrap(Integer id){
        return scrapMapper.deleteScrap(id);
    }

    /**更新报废单**/
    public boolean updateScrap(Scrap scrap){
        boolean isSuccess;
        isSuccess = documentDeviceService.deleteDocumentDeviceByDocId(scrap.getDocument_id());
        for(DocumentDevice documentDevice:scrap.getDocumentDeviceList()){
            isSuccess = isSuccess && documentDeviceService.addDocumentDevice(documentDevice);
        }
        return isSuccess && scrapMapper.updateScrap(scrap);
    }

    /**查询报废单**/
    public List<Scrap> getScrapListParams(String document_id,Integer approve_person_id){
        List<Scrap> scrapList = scrapMapper.getScrapListParams(document_id,approve_person_id);
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }
    public List<Scrap> getScrapListParamsAll(){
        List<Scrap> scrapList = scrapMapper.getScrapListParamsAll();
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }
    public List<Scrap> getScrapListParamsByID(String document_id){
        List<Scrap> scrapList = scrapMapper.getScrapListParamsByID(document_id);
        scrapList = deviceDocumentService.setDevicesAndDocumentDevice(scrapList);
        return scrapList;
    }

    /**根据id查报废单**/
    public Scrap getScrapById(Integer id){
        Scrap scrap = scrapMapper.getScrapById(id);
        scrap = deviceDocumentService.setSingleDevicesAndDocumentDevice(scrap);
        return scrap;
    }

    /**根据id查报废单doc_id**/
    public String getScrapDocIdById(Integer id){
        return scrapMapper.getScrapDocIdById(id);
    }
}
