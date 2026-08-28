package com.cineconnections.domain.entity;

import com.cineconnections.domain.enumtype.CreditType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit")
public class Credit extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    public Person person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    public Movie movie;

    @Column(name = "character_name", length = 500)
    public String characterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false, length = 20)
    public CreditType creditType;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "job")
    public String job;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }

}
