package com.mfajardo.spring_backend_starter;

import com.mfajardo.spring_backend_starter.entity.Role;
import com.mfajardo.spring_backend_starter.exception.ForbiddenOperationException;
import com.mfajardo.spring_backend_starter.repository.RoleRepository;
import com.mfajardo.spring_backend_starter.service.RoleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RoleServiceTest {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void RoleRepository_DeleteAdmin_ThrowError(){
        //Arrange
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        //Act + Assert
        assertThat(roleRepository.findByName("ADMIN")).isPresent();
        assertThatThrownBy(() -> roleService.delete(adminRole.getId()))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage("System role cannot be deleted");
    }




}
