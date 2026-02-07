package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.dto.UserDto;
import com.mfajardo.spring_backend_starter.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> findAll();

    User findById(UUID id);

    User create(UserDto user);

    User update(UUID id, UserDto user);

    void delete(UUID id);
}

