package com.example.demo.db.dao;

import com.example.demo.db.entity.RecordModel;
import com.example.demo.db.entity.RecordModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RecordModelMapper {
    long countByExample(RecordModelExample example);

    int deleteByExample(RecordModelExample example);

    int deleteByPrimaryKey(String key);

    int insert(RecordModel record);

    int insertSelective(RecordModel record);

    List<RecordModel> selectByExample(RecordModelExample example);

    RecordModel selectByPrimaryKey(String key);

    int updateByExampleSelective(@Param("record") RecordModel record, @Param("example") RecordModelExample example);

    int updateByExample(@Param("record") RecordModel record, @Param("example") RecordModelExample example);

    int updateByPrimaryKeySelective(RecordModel record);

    int updateByPrimaryKey(RecordModel record);
}