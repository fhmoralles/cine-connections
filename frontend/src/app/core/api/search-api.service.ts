import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SearchResponse } from '../models/search.model';

@Injectable({
  providedIn: 'root'
})
export class SearchApiService {
  private readonly baseUrl = `${environment.apiUrl}/search`;

  constructor(private readonly http: HttpClient) {}

  search(query: string): Observable<SearchResponse> {
    const params = new HttpParams().set('query', query);

    return this.http.get<SearchResponse>(this.baseUrl, {
      params
    });
  }
}
