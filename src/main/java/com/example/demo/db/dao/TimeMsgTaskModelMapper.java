package com.example.demo.db.dao;

import com.example.demo.db.entity.TimeMsgTaskModel;
import com.example.demo.db.entity.TimeMsgTaskModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TimeMsgTaskModelMapper {
    long countByExample(TimeMsgTaskModelExample example);

    int deleteByExample(TimeMsgTaskModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TimeMsgTaskModel record);

    int insertSelective(TimeMsgTaskModel record);

    List<TimeMsgTaskModel> selectByExample(TimeMsgTaskModelExample example);

    TimeMsgTaskModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TimeMsgTaskModel record, @Param("example") TimeMsgTaskModelExample example);

    int updateByExample(@Param("record") TimeMsgTaskModel record, @Param("example") TimeMsgTaskModelExample example);

    int updateByPrimaryKeySelective(TimeMsgTaskModel record);

    int updateByPrimaryKey(TimeMsgTaskModel record);
}