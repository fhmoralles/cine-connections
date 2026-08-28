CREATE TABLE person (
    id UUID PRIMARY KEY,
    external_id BIGINT UNIQUE,
    name VARCHAR(255) NOT NULL,
    profile_path VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movie (
    id UUID PRIMARY KEY,
    external_id BIGINT UNIQUE,
    title VARCHAR(500) NOT NULL,
    release_date DATE,
    poster_path VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE credit (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    movie_id UUID NOT NULL,
    character_name VARCHAR(500),
    department VARCHAR(100),
    job VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_credit_person
        FOREIGN KEY (person_id)
        REFERENCES person(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_credit_movie
        FOREIGN KEY (movie_id)
        REFERENCES movie(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_person_external_id
    ON person(external_id);

CREATE INDEX idx_movie_external_id
    ON movie(external_id);

CREATE INDEX idx_credit_person_id
    ON credit(person_id);

CREATE INDEX idx_credit_movie_id
    ON credit(movie_id);

CREATE INDEX idx_credit_person_movie
    ON credit(person_id, movie_id);