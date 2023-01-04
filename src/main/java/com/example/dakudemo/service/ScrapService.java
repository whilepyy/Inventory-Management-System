package com.example.dakudemo.service;

import com.example.dakudemo.entity.Scrap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chh
 * @date 2022/3/1 12:14
 */
public interface ScrapService {
    /**增加报废单**/
    public boolean addScrap(Scrap scrap);

    /**删除报废单**/
    public boolean deleteScrap(Integer id);

    /**更新报废单**/
    public boolean updateScrap(Scrap scrap);

    /**查询报废单**/
    public List<Scrap> getScrapListParams(String document_id,Integer approve_person_id);
    public List<Scrap> getScrapListParamsAll();
    public List<Scrap> getScrapListParamsByID(String document_id);
    /**根据id查报废单**/
    public Scrap getScrapById(Integer id);

    /**根据id查报废单doc_id**/
    public String getScrapDocIdById(Integer id);
}
