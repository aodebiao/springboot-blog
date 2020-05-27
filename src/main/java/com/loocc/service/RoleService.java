package com.loocc.service;


import com.loocc.domain.Role;

import java.util.List;

public interface RoleService {
    Role findByRoleName(String roleName);
    List<Role> findAll();
}
