package com.example.demo.db.dao;

import com.example.demo.db.entity.UserInfoModel;
import com.example.demo.db.entity.UserInfoModelExample;
import com.example.demo.db.entity.UserInfoModelKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserInfoModelMapper {
    long countByExample(UserInfoModelExample example);

    int deleteByExample(UserInfoModelExample example);

    int deleteByPrimaryKey(UserInfoModelKey key);

    int insert(UserInfoModel record);

    int insertSelective(UserInfoModel record);

    List<UserInfoModel> selectByExample(UserInfoModelExample example);

    UserInfoModel selectByPrimaryKey(UserInfoModelKey key);

    int updateByExampleSelective(@Param("record") UserInfoModel record, @Param("example") UserInfoModelExample example);

    int updateByExample(@Param("record") UserInfoModel record, @Param("example") UserInfoModelExample example);

    int updateByPrimaryKeySelective(UserInfoModel record);

    int updateByPrimaryKey(UserInfoModel record);
}