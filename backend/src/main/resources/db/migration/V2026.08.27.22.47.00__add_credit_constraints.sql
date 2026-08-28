-- PostgreSQL treats NULLs as distinct in unique indexes, so rows with
-- NULL character_name or job can still collide. Duplicate detection
-- during import remains necessary; this index covers the non-null cases.
CREATE UNIQUE INDEX uq_credit_person_movie_role
ON credit (
    person_id,
    movie_id,
    credit_type,
    character_name,
    job
);
