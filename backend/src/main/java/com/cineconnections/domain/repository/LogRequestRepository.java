package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.LogRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogRequestRepository
        implements PanacheRepositoryBase<LogRequest, Long> {
}
