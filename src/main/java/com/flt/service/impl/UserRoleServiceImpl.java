package com.flt.service.impl;

import com.flt.dal.dao.UserRoleEntityMapper;
import com.flt.dal.entity.UserRoleEntity;
import com.flt.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fenglingtong
 * @date 2018/9/1
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleEntityMapper userRoleEntityMapper;

    @Override
    public UserRoleEntity getUserRoleByUserId(Long userId) {
        return userRoleEntityMapper.selectByPrimaryKey(userId);
    }
}
