package com.example.demo.db.dao;

import com.example.demo.db.entity.GroupInfoModel;
import com.example.demo.db.entity.GroupInfoModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GroupInfoModelMapper {
    long countByExample(GroupInfoModelExample example);

    int deleteByExample(GroupInfoModelExample example);

    int deleteByPrimaryKey(Long groupId);

    int insert(GroupInfoModel record);

    int insertSelective(GroupInfoModel record);

    List<GroupInfoModel> selectByExample(GroupInfoModelExample example);

    GroupInfoModel selectByPrimaryKey(Long groupId);

    int updateByExampleSelective(@Param("record") GroupInfoModel record, @Param("example") GroupInfoModelExample example);

    int updateByExample(@Param("record") GroupInfoModel record, @Param("example") GroupInfoModelExample example);

    int updateByPrimaryKeySelective(GroupInfoModel record);

    int updateByPrimaryKey(GroupInfoModel record);
}