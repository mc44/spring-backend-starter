package com.mfajardo.spring_backend_starter.controller;

import com.mfajardo.spring_backend_starter.projection.ProjectionService;
import com.mfajardo.spring_backend_starter.projection.RoleProjection;
import com.mfajardo.spring_backend_starter.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;
    private final ProjectionService projectionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<List<RoleProjection>> listRoles() {
        return ResponseEntity.ok(
                projectionService.projections(
                        RoleProjection.class,
                        roleService.findAll()
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<RoleProjection> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(
                projectionService.projection(
                        RoleProjection.class,
                        roleService.findById(id)
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleProjection> createRole(
            @RequestParam String name
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        projectionService.projection(
                                RoleProjection.class,
                                roleService.create(name)
                        )
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleProjection> updateRole(
            @PathVariable UUID id,
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                projectionService.projection(
                        RoleProjection.class,
                        roleService.update(id, name)
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/authorities/{authorityId}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<Void> assignAuthority(
            @PathVariable UUID roleId,
            @PathVariable UUID authorityId
    ) {
        roleService.assignAuthority(roleId, authorityId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}/authorities/{authorityId}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<Void> removeAuthority(
            @PathVariable UUID roleId,
            @PathVariable UUID authorityId
    ) {
        roleService.removeAuthority(roleId, authorityId);
        return ResponseEntity.noContent().build();
    }
}
