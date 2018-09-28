package com.flt.dal.dao;

import com.flt.dal.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserEntity record);

    int insertSelective(UserEntity record);

    UserEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserEntity record);

    int updateByPrimaryKey(UserEntity record);
}