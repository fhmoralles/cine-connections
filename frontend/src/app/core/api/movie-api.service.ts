import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { MoviePage } from '../models/movie.model';

export interface ImportedMovie {
  id: string;

  tmdbId: number;

  title: string;
}

@Injectable({
  providedIn: 'root'
})
export class MovieApiService {
  private readonly importUrl = `${environment.apiUrl}/import/movie`;

  private readonly moviesUrl = `${environment.apiUrl}/movies`;

  constructor(private readonly http: HttpClient) {}

  getMoviePage(movieId: string): Observable<MoviePage> {
    return this.http.get<MoviePage>(`${this.moviesUrl}/${movieId}`);
  }

  importMovie(tmdbId: number): Observable<ImportedMovie> {
    return this.http.post<ImportedMovie>(
      `${this.importUrl}/${tmdbId}`,
      {}
    );
  }
}
