package com.loocc.service.impl;


import com.loocc.dao.RoleRepository;
import com.loocc.domain.Role;
import com.loocc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository repository;
    @Override
    public Role findByRoleName(String roleName) {
        return repository.findByName(roleName);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll();
    }
}
