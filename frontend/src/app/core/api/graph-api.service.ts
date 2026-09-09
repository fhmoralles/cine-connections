import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { GraphResponse } from '../models/graph.model';

@Injectable({
  providedIn: 'root'
})
export class GraphApiService {
  private readonly baseUrl = `${environment.apiUrl}/graph`;

  constructor(private readonly http: HttpClient) {}

  getPersonGraph(
    personId: string,
    depth = 2
  ): Observable<GraphResponse> {
    const params = new HttpParams().set('depth', depth);

    return this.http.get<GraphResponse>(
      `${this.baseUrl}/person/${personId}`,
      { params }
    );
  }
}
