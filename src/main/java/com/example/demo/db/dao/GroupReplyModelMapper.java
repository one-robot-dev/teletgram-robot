package com.example.demo.db.dao;

import com.example.demo.db.entity.GroupReplyModel;
import com.example.demo.db.entity.GroupReplyModelExample;
import com.example.demo.db.entity.GroupReplyModelKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GroupReplyModelMapper {
    long countByExample(GroupReplyModelExample example);

    int deleteByExample(GroupReplyModelExample example);

    int deleteByPrimaryKey(GroupReplyModelKey key);

    int insert(GroupReplyModel record);

    int insertSelective(GroupReplyModel record);

    List<GroupReplyModel> selectByExample(GroupReplyModelExample example);

    GroupReplyModel selectByPrimaryKey(GroupReplyModelKey key);

    int updateByExampleSelective(@Param("record") GroupReplyModel record, @Param("example") GroupReplyModelExample example);

    int updateByExample(@Param("record") GroupReplyModel record, @Param("example") GroupReplyModelExample example);

    int updateByPrimaryKeySelective(GroupReplyModel record);

    int updateByPrimaryKey(GroupReplyModel record);
}