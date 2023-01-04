package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Repair;
import com.example.dakudemo.entity.RepairCategory;
import com.example.dakudemo.entity.RepairCause;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @date 2022/1/22 12:23
 */
@Repository
@Mapper
public interface RepairMapper {
    /**添加报检报修单*/
    public Boolean addRepair(Repair repair);

    /**删除报检报修单*/
    public Boolean deleteRepair(Integer id);

    /**修改报检报修单*/
    public Boolean updateRepair(Repair repair);

    /**查询报检报修单列表*/
    public List<Repair> getRepairList();

    /**带参查询报检报修单列表*/
    public List<Repair> getRepairListParams(@Param("document_id") String document_id,@Param("check_repair_category_id") Integer check_repair_category_id,
                                            @Param("document_status")Integer document_status);
    /**查询报检报修种类*/
    public List<RepairCategory> getRepairCategory();

    /**查询报检报修原因*/
    public List<RepairCause> getRepairCause();

    /**根据id查询报检报修表*/
    public Repair getRepairById(Integer id);

}
