import { Component, OnDestroy, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  of,
  switchMap,
  takeUntil
} from 'rxjs';

import { SearchApiService } from '../../core/api/search-api.service';
import { PersonApiService } from '../../core/api/person-api.service';
import { MovieApiService } from '../../core/api/movie-api.service';
import { PageLoadingService } from '../../core/state/page-loading.service';
import { SearchResult } from '../../core/models/search.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './app-header.component.html',
  styleUrl: './app-header.component.scss'
})
export class AppHeaderComponent implements OnDestroy {
  searchControl = new FormControl('', {
    nonNullable: true
  });

  results = signal<SearchResult[]>([]);

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly searchApi: SearchApiService,
    private readonly personApi: PersonApiService,
    private readonly movieApi: MovieApiService,
    private readonly pageLoading: PageLoadingService,
    private readonly router: Router
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

          return this.searchApi.search(value);
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

  goHome(): void {
    this.results.set([]);
    this.searchControl.setValue('', { emitEvent: false });
    this.pageLoading.stop();
  }

  selectResult(result: SearchResult): void {
    this.results.set([]);

    this.searchControl.setValue(result.name, {
      emitEvent: false
    });

    if (result.type === 'PERSON') {
      this.selectPerson(result);
      return;
    }

    this.selectMovie(result);
  }

  private selectPerson(result: SearchResult): void {
    if (result.imported && result.id) {
      void this.router.navigate(['/person', result.id], {
        queryParams: { tmdbId: result.tmdbId }
      });
      return;
    }

    this.pageLoading.start('Importing filmography...');

    this.personApi.importPerson(result.tmdbId).subscribe({
      next: person => {
        void this.router.navigate(['/person', person.id]);
      },
      error: () => {
        this.pageLoading.stop();
      }
    });
  }

  private selectMovie(result: SearchResult): void {
    if (result.imported && result.id) {
      void this.router.navigate(['/movie', result.id]);
      return;
    }

    this.pageLoading.start('Importing movie...');

    this.movieApi.importMovie(result.tmdbId).subscribe({
      next: movie => {
        void this.router.navigate(['/movie', movie.id]);
      },
      error: () => {
        this.pageLoading.stop();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
