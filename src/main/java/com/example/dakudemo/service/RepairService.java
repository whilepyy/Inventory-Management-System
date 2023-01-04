package com.example.dakudemo.service;

import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Repair;
import com.example.dakudemo.entity.RepairCategory;
import com.example.dakudemo.entity.RepairCause;

import java.util.List;

/**
 * @author yfgan
 * @date 2022/1/22 18:20
 */
public interface RepairService {
    /**添加报检报修*/
    public Boolean addRepair(Repair repair);

    /**删除报检报修*/
    public Boolean deleteRepair(Integer id);

    /**修改报检报修*/
    public Boolean updateRepair(Repair repair);

    /**查询报检报修列表*/
    public List<Repair> getRepairList();
    /**带参查询报检报修列表*/
    public List<Repair> getRepairListParams(String document_id,Integer check_repair_category_id,Integer document_status);


    /**查询报检报修类别*/
    public List<RepairCategory> getRepairCategoryList();
    /**查询报检报修事由*/
    public List<RepairCause> getRepairCauseList();

    /**根据id查询报检报修表*/
    public Repair getRepairById(Integer id);
}
