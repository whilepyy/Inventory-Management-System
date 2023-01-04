package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DeviceCategory;
import com.example.dakudemo.entity.Inout;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yfgan
 * @create 2021-12-29 11:05
 */
@Repository
@Mapper
public interface InoutMapper {
    /**添加入库*/
    public Boolean addIn(Inout inout);

    /**删除入库*/
    public Boolean deleteIn(Integer id);

    /**修改入库*/
    public Boolean updateIn(Inout inout);
    public Boolean updateInNew(@Param("inout") Inout inout,@Param("id") Integer id);

    /**带参查询in列表*/
    public List<Inout> getInListParams(@Param("document_id") String document_id,@Param("document_category_id") Integer document_category_id, @Param("document_status") Integer document_status, @Param("buy_use_person_id") Integer buy_use_person_id);
    /**查询in列表*/
    public List<Inout> getInList(@Param("document_category_id") Integer document_category_id);

    /**添加出库*/
    public Boolean addOut(Inout inout);
    /**删除出库*/
    public Boolean deleteOut(Integer id);

    /**修改出库*/
    public Boolean updateOut(Inout inout);

    /**带参查询out列表*/
    public List<Inout> getOutListParams(@Param("document_id") String document_id,@Param("document_category_id") Integer document_category_id,
                                        @Param("buy_use_person_id") Integer buy_use_person_id, @Param("document_status") Integer document_status);
    /**查询out列表*/
    public List<Inout> getOutList(@Param("document_category_id") Integer document_category_id);

    /** 根据id查询out */
    public Inout getInoutById(Integer id);

}
