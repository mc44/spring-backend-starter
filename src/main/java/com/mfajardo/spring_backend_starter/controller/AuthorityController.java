package com.mfajardo.spring_backend_starter.controller;

import com.mfajardo.spring_backend_starter.projection.AuthorityProjection;
import com.mfajardo.spring_backend_starter.projection.ProjectionService;
import com.mfajardo.spring_backend_starter.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authorities")
@PreAuthorize("hasAuthority('ROLE_ASSIGN')")
public class AuthorityController {

    private final AuthorityService authorityService;
    private final ProjectionService projectionService;

    @GetMapping
    public ResponseEntity<List<AuthorityProjection>> listAuthorities() {
        return ResponseEntity.ok(
                projectionService.projections(
                        AuthorityProjection.class,
                        authorityService.findAll()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorityProjection> getAuthority(@PathVariable UUID id) {
        return ResponseEntity.ok(
                projectionService.projection(
                        AuthorityProjection.class,
                        authorityService.findById(id)
                )
        );
    }
}
