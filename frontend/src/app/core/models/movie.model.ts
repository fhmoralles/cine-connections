export interface MovieSummary {
  id: string;
  tmdbId: number;
  title: string;
  releaseYear: number | null;
  posterPath: string | null;
  backdropPath: string | null;
}

export interface MovieCastMember {
  personId: string;
  tmdbId: number;
  name: string;
  characterName: string | null;
  profilePath: string | null;
  order: number | null;
}

export interface MoviePage {
  movie: MovieSummary;
  cast: MovieCastMember[];
  similarMovies: MovieSummary[];
  otherMoviesWithCast: MovieSummary[];
}
