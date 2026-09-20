import { Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EMPTY, switchMap } from 'rxjs';

import { PersonApiService } from '../../core/api/person-api.service';
import { PersonPage } from '../../core/models/person.model';
import { PageLoadingService } from '../../core/state/page-loading.service';
import { tmdbImage } from '../../core/utils/tmdb-image';
import { ComingSoonHintComponent } from '../../shared/coming-soon-hint/coming-soon-hint.component';

@Component({
  selector: 'app-person-page',
  standalone: true,
  imports: [RouterLink, ComingSoonHintComponent],
  templateUrl: './person-page.component.html',
  styleUrl: './person-page.component.scss'
})
export class PersonPageComponent {
  private readonly route = inject(ActivatedRoute);

  private readonly personApi = inject(PersonApiService);

  private readonly pageLoading = inject(PageLoadingService);

  private readonly moviesTrack =
    viewChild<ElementRef<HTMLDivElement>>('moviesTrack');

  private readonly coActorsTrack =
    viewChild<ElementRef<HTMLDivElement>>('coActorsTrack');

  page = signal<PersonPage | null>(null);

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap(params => {
          const personId = params.get('id');

          this.page.set(null);

          if (!personId) {
            this.pageLoading.stop();
            return EMPTY;
          }

          this.pageLoading.start('Importing filmography...');

          return this.personApi.getPersonPage(personId);
        }),
        takeUntilDestroyed()
      )
      .subscribe({
        next: page => {
          this.page.set(page);
          this.pageLoading.stop();
        },
        error: () => {
          this.page.set(null);
          this.pageLoading.stop();
        }
      });
  }

  posterUrl(path: string | null | undefined, size: 'w185' | 'w342' | 'w500' = 'w185'): string | null {
    return tmdbImage(path, size);
  }

  profileUrl(path: string | null | undefined, size: 'w185' | 'w500' = 'w185'): string | null {
    return tmdbImage(path, size);
  }

  backdropStyle(page: PersonPage): string {
    const backdrop = page.movies.find(movie => movie.backdropPath)?.backdropPath
      ?? page.movies.find(movie => movie.posterPath)?.posterPath
      ?? page.person.profilePath;

    const url = tmdbImage(backdrop, 'original');

    if (!url) {
      return 'none';
    }

    return `url('${url}')`;
  }

  initials(name: string): string {
    return name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part.charAt(0))
      .join('')
      .toUpperCase();
  }

  sharedLabel(count: number): string {
    return count === 1 ? '1 movie' : `${count} movies`;
  }

  scrollRow(kind: 'movies' | 'coActors', direction: -1 | 1): void {
    const track =
      kind === 'movies'
        ? this.moviesTrack()?.nativeElement
        : this.coActorsTrack()?.nativeElement;

    track?.scrollBy({
      left: direction * 420,
      behavior: 'smooth'
    });
  }
}
