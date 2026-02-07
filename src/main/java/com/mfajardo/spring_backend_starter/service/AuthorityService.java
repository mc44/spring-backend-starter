package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.entity.Authority;

import java.util.List;
import java.util.UUID;

public interface AuthorityService {

    List<Authority> findAll();

    Authority findById(UUID id);

}
