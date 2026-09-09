import { Component, OnDestroy, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  finalize,
  of,
  switchMap,
  takeUntil
} from 'rxjs';

import { PersonApiService } from '../../core/api/person-api.service';
import { GraphApiService } from '../../core/api/graph-api.service';
import { PersonSummary } from '../../core/models/person.model';
import { GraphResponse } from '../../core/models/graph.model';
import { GraphExplorerComponent } from '../graph/graph-explorer/graph-explorer.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [ReactiveFormsModule, GraphExplorerComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent implements OnDestroy {
  searchControl = new FormControl('', {
    nonNullable: true
  });

  results = signal<PersonSummary[]>([]);

  graph = signal<GraphResponse | null>(null);

  loading = signal(false);

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly personApi: PersonApiService,
    private readonly graphApi: GraphApiService
  ) {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => {
          if (query.trim().length < 2) {
            this.results.set([]);
            return of([]);
          }

          return this.personApi.search(query);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(results => {
        this.results.set(results);
      });
  }

  selectPerson(person: PersonSummary): void {
    this.loading.set(true);
    this.graph.set(null);
    this.results.set([]);

    this.searchControl.setValue(person.name, { emitEvent: false });

    this.graphApi
      .getPersonGraph(person.id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: graph => {
          this.graph.set(graph);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
