package com.mfajardo.spring_backend_starter.projection;

import java.util.Set;
import java.util.UUID;

public interface RoleProjection {
    UUID getId();
    String getName();
    Set<AuthorityProjection> getAuthorities();
}
