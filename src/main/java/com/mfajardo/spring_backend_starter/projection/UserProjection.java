package com.mfajardo.spring_backend_starter.projection;

import com.mfajardo.spring_backend_starter.entity.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = User.class)
public interface UserProjection {
    String getId();
    String getUsername();
    String getEmail();
}
