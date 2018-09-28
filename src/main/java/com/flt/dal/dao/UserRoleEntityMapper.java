package com.flt.dal.dao;

import com.flt.dal.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleEntity record);

    int insertSelective(UserRoleEntity record);

    UserRoleEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoleEntity record);

    int updateByPrimaryKey(UserRoleEntity record);
}