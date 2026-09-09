import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ImportedMovie {
  id: string;

  tmdbId: number;

  title: string;
}

@Injectable({
  providedIn: 'root'
})
export class MovieApiService {
  private readonly baseUrl = `${environment.apiUrl}/import/movie`;

  constructor(private readonly http: HttpClient) {}

  importMovie(tmdbId: number): Observable<ImportedMovie> {
    return this.http.post<ImportedMovie>(
      `${this.baseUrl}/${tmdbId}`,
      {}
    );
  }
}
