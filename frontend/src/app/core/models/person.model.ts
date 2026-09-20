import { MovieSummary } from './movie.model';

export interface PersonSummary {

  id: string | null;

  tmdbId: number;

  name: string;

  profilePath?: string;

  imported: boolean;
}

export interface PersonSearchResponse {

  results: PersonSummary[];

  imported: boolean;
}

export interface ImportedPerson {

  id: string;

  tmdbId: number;

  name: string;
}

export interface PersonProfile {
  id: string;
  tmdbId: number;
  name: string;
  profilePath: string | null;
}

export interface CoActor {
  personId: string;
  tmdbId: number;
  name: string;
  profilePath: string | null;
  sharedMovieCount: number;
}

export interface PersonPage {
  person: PersonProfile;
  movies: MovieSummary[];
  frequentCoActors: CoActor[];
  movieCount: number;
  coActorCount: number;
}
