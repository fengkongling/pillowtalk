package com.flt.service.impl;

import com.flt.dal.dao.UserEntityMapper;
import com.flt.dal.entity.UserEntity;
import com.flt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fenglingtong
 * @date 2018/9/1
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Override
    public UserEntity getUserById(Long id) {
        return userEntityMapper.selectByPrimaryKey(id);
    }
}
