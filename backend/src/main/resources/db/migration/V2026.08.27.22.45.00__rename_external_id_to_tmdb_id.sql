ALTER TABLE person
    RENAME COLUMN external_id TO tmdb_id;

ALTER TABLE movie
    RENAME COLUMN external_id TO tmdb_id;

ALTER INDEX idx_person_external_id
    RENAME TO idx_person_tmdb_id;

ALTER INDEX idx_movie_external_id
    RENAME TO idx_movie_tmdb_id;
