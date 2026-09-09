export type SearchResultType =
  | 'PERSON'
  | 'MOVIE';

export interface SearchResult {
  type: SearchResultType;

  id: string | null;

  tmdbId: number;

  name: string;

  secondaryInfo: string;

  imagePath?: string;

  imported: boolean;
}

export interface SearchResponse {
  results: SearchResult[];
}
