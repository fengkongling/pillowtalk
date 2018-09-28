package com.flt.service;

import com.flt.dal.entity.UserEntity;

/**
 * @author fenglingtong
 * @date 2018/9/1
 */
public interface UserService {
    UserEntity getUserById(Long id);
}
