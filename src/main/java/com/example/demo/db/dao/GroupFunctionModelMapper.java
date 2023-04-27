package com.example.demo.db.dao;

import com.example.demo.db.entity.GroupFunctionModel;
import com.example.demo.db.entity.GroupFunctionModelExample;
import com.example.demo.db.entity.GroupFunctionModelKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GroupFunctionModelMapper {
    long countByExample(GroupFunctionModelExample example);

    int deleteByExample(GroupFunctionModelExample example);

    int deleteByPrimaryKey(GroupFunctionModelKey key);

    int insert(GroupFunctionModel record);

    int insertSelective(GroupFunctionModel record);

    List<GroupFunctionModel> selectByExample(GroupFunctionModelExample example);

    GroupFunctionModel selectByPrimaryKey(GroupFunctionModelKey key);

    int updateByExampleSelective(@Param("record") GroupFunctionModel record, @Param("example") GroupFunctionModelExample example);

    int updateByExample(@Param("record") GroupFunctionModel record, @Param("example") GroupFunctionModelExample example);

    int updateByPrimaryKeySelective(GroupFunctionModel record);

    int updateByPrimaryKey(GroupFunctionModel record);
}