ALTER TABLE movie
ADD COLUMN backdrop_path VARCHAR(500);

ALTER TABLE credit
ADD COLUMN cast_order INTEGER;

CREATE TABLE movie_genre (
    movie_id UUID NOT NULL,
    tmdb_genre_id INTEGER NOT NULL,

    CONSTRAINT pk_movie_genre
        PRIMARY KEY (movie_id, tmdb_genre_id),

    CONSTRAINT fk_movie_genre_movie
        FOREIGN KEY (movie_id)
        REFERENCES movie(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_movie_genre_tmdb_genre_id
    ON movie_genre (tmdb_genre_id);

CREATE INDEX idx_credit_cast_order
    ON credit (movie_id, credit_type, cast_order);
