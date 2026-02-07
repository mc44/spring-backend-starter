package com.mfajardo.spring_backend_starter.projection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class ProjectionService {

    @Bean
    public ProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }

    public <S, T> S projection(Class<S> projectionType, T entity) {
        return this.projectionFactory().createProjection(projectionType, entity);
    }

    public <S, T> List<S> projections(Class<S> projectionType, List<T> entities) {
        List<S> projections = new ArrayList<>();

        for (T entity : entities) {
            projections.add(this.projection(projectionType, entity));
        }

        return projections;
    }

    public <S, T> Set<S> projections(Class<S> projectionType, Set<T> entities) {
        Set<S> projections = new HashSet<>();

        for (T entity : entities) {
            projections.add(this.projection(projectionType, entity));
        }

        return projections;
    }

}

