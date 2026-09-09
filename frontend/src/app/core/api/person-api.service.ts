import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { PersonSummary } from '../models/person.model';

@Injectable({
  providedIn: 'root'
})
export class PersonApiService {
  private readonly baseUrl = `${environment.apiUrl}/persons`;

  constructor(private readonly http: HttpClient) {}

  search(query: string): Observable<PersonSummary[]> {
    const params = new HttpParams().set('query', query);

    return this.http.get<PersonSummary[]>(`${this.baseUrl}/search`, {
      params
    });
  }
}
