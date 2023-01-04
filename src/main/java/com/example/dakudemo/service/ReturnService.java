package com.example.dakudemo.service;

import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Return;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chh
 * @date 2022/1/23 15:02
 */
public interface ReturnService {
    /**添加归还*/
    public Boolean addReturn(Return ret);

    /**删除归还*/
    public Boolean deleteReturn(Integer id);

    /**修改归还*/
    public Boolean updateReturn(Return ret);

    /**根据document_id查询归还*/
    public List<Return> getReturnListByDocId(String document_id);

    /**根据return_person_id查询归还*/
    public List<Return> getReturnListByUserId(String return_person_id);

    /**根据id查询借出*/
    public Return getReturnById(Integer id);

    /**根据id查询借出document_id*/
    public String getReturnDocIdById(Integer id);

    /**查询借出列表*/
    public List<Return> getReturnList(String document_id,Integer return_person_id, String return_person_name);

    public List<Return> getReturnListAll();
    public List<Return> getReturnListById(String document_id);
}
