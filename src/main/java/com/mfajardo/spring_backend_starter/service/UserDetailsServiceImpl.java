package com.mfajardo.spring_backend_starter.service;

import com.mfajardo.spring_backend_starter.config.SecurityUser;
import com.mfajardo.spring_backend_starter.dto.UserDto;
import com.mfajardo.spring_backend_starter.entity.Role;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.repository.RoleRepository;
import com.mfajardo.spring_backend_starter.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userInfoRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username: " + username
                        )
                );
        return new SecurityUser(user);
    }

    public User addUser(UserDto userDto) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException("Default USER role not found")
                );
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(encoder.encode(userDto.getPassword()));
        newUser.setEmail(userDto.getEmail());
         newUser.setRoles(Set.of(userRole));
        userInfoRepository.save(newUser);
        return newUser;
    }

}
