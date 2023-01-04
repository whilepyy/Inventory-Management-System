package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Restock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RestockMapper {

    /**添加报检报修返还单*/
    public Boolean addRestock(Restock restock);

    /**删除报检报修返回单*/
    public Boolean deleteRestock(Integer id);

    /**更新报检报修返回单*/
    public Boolean updateRestock(Restock restock);

    /**查询报检报修返回单*/
    public List<Restock> getRestockListParams(@Param("id") Integer id, @Param("document_id")String document_id,
                                           @Param("check_repair_document_id")String check_repair_document_id,
                                           @Param("document_person_id")Integer document_person_id,
                                              @Param("document_status")Integer document_status);
    /**查询报检报修返回单*/
    public List<Restock> getRestockList();
}
