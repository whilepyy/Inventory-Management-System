package com.example.dakudemo.service;

import com.example.dakudemo.entity.Lend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chh
 * @date 2022/1/22 18:20
 */
public interface LendService {
    /**添加借出*/
    public Boolean addLend(Lend lend);

    /**删除借出*/
    public Boolean deleteLend(Integer id);

    /**修改借出*/
    public Boolean updateLend(Lend lend);

    /**根据document_id查询借出*/
    public List<Lend> getLendListByDocId(String document_id);

    /**根据device_user_id查询借出*/
    public List<Lend> getLendListByUserId(String device_user_id);

    /**根据id查询借出*/
    public Lend getLendById(Integer id);

    /**根据id查询借出document_id*/
    public String getLendDocIdById(Integer id);

    /**查询借出列表*/
    public List<Lend> getLendList(String document_id,Integer device_user_id,Integer document_status);

    public List<Lend> getLendListAll();
    public List<Lend> getLendListById(String document_id);
}
