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
