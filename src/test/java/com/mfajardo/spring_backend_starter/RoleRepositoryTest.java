package com.mfajardo.spring_backend_starter;

import com.mfajardo.spring_backend_starter.entity.Role;
import com.mfajardo.spring_backend_starter.repository.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop" // or update if you prefer
})
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void RoleRepository_SaveAll_ReturnSavedRole(){
        //Arrange
            Role testRole = Role.builder().name("Test").build();
        //Act
            Role savedRole = roleRepository.save(testRole);
        //Assert
            Assertions.assertThat(savedRole).isNotNull();
    }

    @Test
    public void RoleRepository_GetAll_ReturnSavedRole(){
        //Arrange
        Role testRole = Role.builder().name("Test").build();
        Role testRole1 = Role.builder().name("Test1").build();
        //Act
        roleRepository.save(testRole);
        roleRepository.save(testRole1);

        List<Role> roleList = roleRepository.findAll();

        //Assert
        Assertions.assertThat(roleList).isNotNull();
        Assertions.assertThat(roleList).hasSize(2);
    }

}
