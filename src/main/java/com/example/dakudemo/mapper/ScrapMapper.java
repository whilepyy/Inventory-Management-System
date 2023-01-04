package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Scrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chh
 * @date 2022/2/28 22:35
 */
@Repository
@Mapper
public interface ScrapMapper {
    /**增加报废单**/
    public boolean addScrap(Scrap scrap);

    /**删除报废单**/
    public boolean deleteScrap(Integer id);

    /**更新报废单**/
    public boolean updateScrap(Scrap scrap);

    /**查询报废单**/
    public List<Scrap> getScrapListParams(@Param("document_id") String document_id,
                                          @Param("document_status")Integer document_status);
    public List<Scrap> getScrapListParamsAll();
    public List<Scrap> getScrapListParamsByID(@Param("document_id") String document_id);
    /**根据id查报废单**/
    public Scrap getScrapById(Integer id);

    /**根据id查报废单doc_id**/
    public String getScrapDocIdById(Integer id);
}
