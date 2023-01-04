package com.example.dakudemo.service;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.Inout;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 15:09
 */
public interface InoutService {
    /**带参查询in列表信息**/
    List<Inout> getInListParams(String document_id,Integer document_category_id, Integer document_status, Integer buy_use_person_id);
    /**查询in列表信息**/
    List<Inout> getInList(Integer document_category_id);
    /**新增in**/
    public Boolean addIn(Inout inout);
    /**修改in**/
    public Boolean updateIn(Inout inout);
    public Boolean updateInNew(Inout inout,Integer id);

    /**删除in**/
    public Boolean deleteIn(Integer id);

    /**带参查询out列表信息**/
    List<Inout> getOutListParams(String document_id,Integer document_category_id,Integer buy_use_person_id, Integer document_status);
    /**查询out列表信息**/
    List<Inout> getOutList(Integer document_category_id);
    /**新增out**/
    public Boolean addOut(Inout inout);
    /**修改out**/
    public Boolean updateOut(Inout inout);
    /**删除out**/
    public Boolean deleteOut(Integer id);

    /** 根据id查询out */
    public Inout getInoutById(Integer id);

}
