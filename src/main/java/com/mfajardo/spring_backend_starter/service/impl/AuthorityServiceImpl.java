package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.entity.Authority;
import com.mfajardo.spring_backend_starter.repository.AuthorityRepository;
import com.mfajardo.spring_backend_starter.service.AuthorityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional()
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    @Override
    public Authority findById(UUID id) {
        return authorityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found"));
    }
}
