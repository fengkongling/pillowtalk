package com.flt.service;

import com.flt.dal.entity.UserRoleEntity;

/**
 * @author fenglingtong
 * @date 2018/9/1
 */
public interface UserRoleService {
    UserRoleEntity getUserRoleByUserId(Long userId);
}
