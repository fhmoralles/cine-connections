package com.cineconnections.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "log_request")
public class LogRequest extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(name = "context_method", nullable = false)
    public String contextMethod;

    @Column(name = "context_headers")
    public String contextHeaders;

    @Column(name = "context_body")
    public String contextBody;

    @Column(name = "request_remote_address")
    public String requestRemoteAddress;

    @Column(name = "request_cookie_map")
    public String requestCookieMap;

    @Column(name = "info_path")
    public String infoPath;

    @Column(name = "info_path_parameters")
    public String infoPathParameters;

    @Column(name = "info_query_parameters")
    public String infoQueryParameters;

    @Column(name = "duration")
    public Integer duration;

    @Column(name = "request_origin")
    public String requestOrigin;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}
