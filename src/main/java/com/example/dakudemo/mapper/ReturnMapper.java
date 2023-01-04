package com.example.dakudemo.mapper;

import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.entity.Return;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chh
 * @date 2022/1/22 20:46
 */
@Repository
@Mapper
public interface ReturnMapper {
    /**添加归还*/
    public Boolean addReturn(Return ret);

    /**删除归还*/
    public Boolean deleteReturn(Integer id);

    /**修改归还*/
    public Boolean updateReturn(Return ret);

    /**根据document_id查询归还*/
    public List<Return> getReturnListByDocId(@Param("document_id") String document_id);

    /**根据return_person_id查询归还*/
    public List<Return> getReturnListByUserId(@Param("return_person_id") String return_person_id);

    /**根据id查询借出*/
    public Return getReturnById(@Param("id") Integer id);

    /**根据id查询借出document_id*/
    public String getReturnDocIdById(@Param("id") Integer id);

    /**查询借出列表*/
    public List<Return> getReturnList(@Param("document_id") String document_id,@Param("return_person_id") Integer return_person_id,
                                      @Param("return_person_name")String return_person_name);
    public List<Return> getReturnListAll();
    public List<Return> getReturnListById(@Param("document_id") String document_id);
}
