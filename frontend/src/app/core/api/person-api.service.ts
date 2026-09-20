import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  ImportedPerson,
  PersonPage,
  PersonSearchResponse
} from '../models/person.model';

@Injectable({
  providedIn: 'root'
})
export class PersonApiService {
  private readonly baseUrl = `${environment.apiUrl}/persons`;

  constructor(private readonly http: HttpClient) {}

  search(query: string): Observable<PersonSearchResponse> {
    const params = new HttpParams().set('query', query);

    return this.http.get<PersonSearchResponse>(`${this.baseUrl}/search`, {
      params
    });
  }

  getPersonPage(personId: string): Observable<PersonPage> {
    return this.http.get<PersonPage>(`${this.baseUrl}/${personId}`);
  }

  importPerson(tmdbId: number): Observable<ImportedPerson> {
    return this.http.post<ImportedPerson>(
      `${environment.apiUrl}/import/person/${tmdbId}`,
      {}
    );
  }
}
