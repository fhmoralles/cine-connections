package com.cineconnections.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "movie")
public class Movie extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "tmdb_id", unique = true)
    public Long tmdbId;

    @Column(nullable = false, length = 500)
    public String title;

    @Column(name = "release_date")
    public LocalDate releaseDate;

    @Column(name = "poster_path", length = 500)
    public String posterPath;

    @Column(name = "backdrop_path", length = 500)
    public String backdropPath;

    @Column(name = "cast_expanded", nullable = false)
    public boolean castExpanded = false;

    @ElementCollection
    @CollectionTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id")
    )
    @Column(name = "tmdb_genre_id", nullable = false)
    public Set<Integer> genreIds = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @OneToMany(mappedBy = "movie")
    public Set<Credit> credits = new HashSet<>();

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addGenreIds(Collection<Integer> ids) {

        if (ids == null || ids.isEmpty()) {
            return;
        }

        if (genreIds == null) {
            genreIds = new HashSet<>();
        }

        for (Integer genreId : ids) {
            if (genreId != null) {
                genreIds.add(genreId);
            }
        }
    }

}
