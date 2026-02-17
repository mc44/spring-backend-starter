package com.mfajardo.spring_backend_starter.service.impl;

import com.mfajardo.spring_backend_starter.dto.UserDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.exception.EntityNotFoundException;
import com.mfajardo.spring_backend_starter.exception.ForbiddenOperationException;
import com.mfajardo.spring_backend_starter.repository.UserInfoRepository;
import com.mfajardo.spring_backend_starter.service.UserDetailsServiceImpl;
import com.mfajardo.spring_backend_starter.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User create(UserDto userDto) {
        return userDetailsService.addUser(userDto);
    }

    @Override
    public User update(UUID id, UserDto updated) {
        User existing = findById(id);
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setGender(updated.getGender());
        return existing;
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (isAdmin) {
            throw new ForbiddenOperationException("Main admin cannot be deleted");
        }

        userRepository.delete(user);
    }

}
