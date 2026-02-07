package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.entity.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<Role> findAll();

    Role findById(UUID id);

    Role create(String name);

    Role update(UUID id, String name);

    void delete(UUID id);

    void assignAuthority(UUID roleId, UUID authorityId);

    void removeAuthority(UUID roleId, UUID authorityId);
}
