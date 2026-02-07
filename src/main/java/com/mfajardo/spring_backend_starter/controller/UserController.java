package com.mfajardo.spring_backend_starter.controller;

import com.mfajardo.spring_backend_starter.dto.UserDto;
import com.mfajardo.spring_backend_starter.entity.User;
import com.mfajardo.spring_backend_starter.projection.ProjectionService;
import com.mfajardo.spring_backend_starter.projection.UserProjection;
import com.mfajardo.spring_backend_starter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ProjectionService projectionService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserProjection>> listUsers() {
        return ResponseEntity.ok(projectionService.projections(UserProjection.class, userService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserProjection> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(projectionService.projection(UserProjection.class, userService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserProjection> createUser(
            @RequestBody @Valid UserDto userDto
    ) {
        User created = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectionService.projection(UserProjection.class, created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserProjection> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(projectionService.projection(UserProjection.class, userService.update(id, userDto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }}
