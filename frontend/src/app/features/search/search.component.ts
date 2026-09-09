import { Component, OnDestroy, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
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

  loadingMessage = signal('Building connections...');

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
          const value = query.trim();

          if (value.length < 2) {
            this.results.set([]);
            return of(null);
          }

          return this.personApi.search(value);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(response => {
        if (!response) {
          return;
        }

        this.results.set(response.results);
      });
  }

  selectPerson(person: PersonSummary): void {
    this.loading.set(true);
    this.graph.set(null);
    this.results.set([]);

    this.searchControl.setValue(person.name, { emitEvent: false });

    if (person.imported && person.id) {
      this.loadingMessage.set('Building connections...');
      this.loadGraph(person.id);
      return;
    }

    this.loadingMessage.set('Importing filmography...');

    this.personApi.importPerson(person.tmdbId).subscribe({
      next: importedPerson => {
        this.loadingMessage.set('Building connections...');
        this.loadGraph(importedPerson.id);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  private loadGraph(personId: string): void {
    this.loadingMessage.set('Building connections...');

    this.graphApi.getPersonGraph(personId, 2).subscribe({
      next: graph => {
        this.graph.set(graph);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
