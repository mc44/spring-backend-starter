package com.mfajardo.spring_backend_starter;

import com.mfajardo.spring_backend_starter.entity.Authority;
import com.mfajardo.spring_backend_starter.entity.Role;
import com.mfajardo.spring_backend_starter.exception.EntityNotFoundException;
import com.mfajardo.spring_backend_starter.repository.AuthorityRepository;
import com.mfajardo.spring_backend_starter.repository.RoleRepository;
import com.mfajardo.spring_backend_starter.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceMockTest {
    @Mock
    RoleRepository roleRepository;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    private UUID roleId;
    private UUID authorityId;

    @BeforeEach
    void setup() {
        roleId = UUID.randomUUID();
        authorityId = UUID.randomUUID();
    }

    @Test
    void assignAuthority_Success(){

        Role role = Role.builder().name("SampleRole").authorities(new HashSet<>()).build();
        Authority authority = Authority.builder().name("SampleAuthority").build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(authority));

        roleService.assignAuthority(roleId, authorityId);

        assertTrue(role.getAuthorities().contains(authority));

        verify(roleRepository).findById(roleId);
        verify(authorityRepository).findById(authorityId);
    }

    @Test
    void assignAuthority_RoleNotFound(){

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->  roleService.assignAuthority(roleId, authorityId));

        assertEquals("Role not found", ex.getMessage());

        verify(roleRepository).findById(roleId);
        verifyNoInteractions(authorityRepository);
    }

    @Test
    void assignAuthority_AuthorityNotFound() {

        Role role = Role.builder().name("SampleRole").authorities(new HashSet<>()).build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(authorityRepository.findById(authorityId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () ->  roleService.assignAuthority(roleId, authorityId));

        assertEquals("Authority not found", ex.getMessage());

        verify(roleRepository).findById(roleId);
        verify(authorityRepository).findById(authorityId);
    }

}
