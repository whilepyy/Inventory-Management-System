package com.example.dakudemo.service;

import com.example.dakudemo.entity.Restock;
import com.example.dakudemo.entity.Result;


import java.util.List;

public interface RestockService {
    /**添加报检报修返还单*/
    public Result addRestock(Restock restock);

    /**删除报检报修返回单*/
    public Result deleteRestock(Integer id);

    /**更新报检报修返回单*/
    public Result updateRestock(Restock restock);

    /**查询报检报修返回单*/
    public List<Restock> getRestockListParams(Integer id, String document_id,
                                              String check_repair_document_id,
                                              Integer document_person_id,
                                              Integer document_status);
    /**查询报检报修返回单*/
    public List<Restock> getRestockList();
}
