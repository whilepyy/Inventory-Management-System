package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Lend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * @author chh
 * @date 2022/1/22 12:23
 */
@Repository
@Mapper
public interface LendMapper {
    /**添加借出*/
    public Boolean addLend(Lend lend);

    /**删除借出*/
    public Boolean deleteLend(Integer id);

    /**修改借出*/
    public Boolean updateLend(Lend lend);

    /**根据document_id查询借出*/
    public List<Lend> getLendListByDocId(@Param("document_id") String document_id);

    /**根据device_user_id查询借出*/
    public List<Lend> getLendListByUserId(@Param("device_user_id") String device_user_id);

    /**根据id查询借出*/
    public Lend getLendById(@Param("id") Integer id);

    /**根据id查询借出document_id*/
    public String getLendDocIdById(@Param("id") Integer id);

    /**查询借出列表*/
    public List<Lend> getLendList(@Param("document_id") String document_id, @Param("device_user_id") Integer device_user_id, @Param("document_status")Integer document_status);
    public List<Lend> getLendListAll();
    public List<Lend> getLendListById(@Param("document_id") String document_id);
}
