ALTER TABLE movie
ADD COLUMN cast_expanded BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX
IF NOT EXISTS
ux_person_tmdb_id
ON person (tmdb_id);

CREATE UNIQUE INDEX
IF NOT EXISTS
ux_movie_tmdb_id
ON movie (tmdb_id);
