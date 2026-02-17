package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.entity.Authority;
import com.mfajardo.spring_backend_starter.entity.Role;
import com.mfajardo.spring_backend_starter.exception.EntityNotFoundException;
import com.mfajardo.spring_backend_starter.exception.ForbiddenOperationException;
import com.mfajardo.spring_backend_starter.repository.AuthorityRepository;
import com.mfajardo.spring_backend_starter.repository.RoleRepository;
import com.mfajardo.spring_backend_starter.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }

    @Override
    public Role create(String name) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists");
        }

        Role role = new Role();
        role.setName(name);

        return roleRepository.save(role);
    }

    @Override
    public Role update(UUID id, String name) {
        Role role = findById(id);

        if ("ADMIN".equals(role.getName())) {
            throw new ForbiddenOperationException("System role cannot be renamed");
        }

        role.setName(name);
        return role;
    }

    @Override
    public void delete(UUID id) {
        Role role = findById(id);

        if ("ADMIN".equals(role.getName())) {
            throw new ForbiddenOperationException("System role cannot be deleted");
        }

        roleRepository.delete(role);
    }

    @Override
    public void assignAuthority(UUID roleId, UUID authorityId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found"));

        role.getAuthorities().add(authority);
    }

    @Override
    public void removeAuthority(UUID roleId, UUID authorityId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found"));

        if ("ADMIN".equals(role.getName())) {
            throw new IllegalStateException("Cannot remove authorities from ADMIN role");
        }

        role.getAuthorities().remove(authority);
    }
}
