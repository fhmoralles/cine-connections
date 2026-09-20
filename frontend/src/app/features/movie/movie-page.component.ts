import { Component, ElementRef, computed, inject, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EMPTY, switchMap } from 'rxjs';

import { MovieApiService } from '../../core/api/movie-api.service';
import { MovieCastMember, MoviePage, MovieSummary } from '../../core/models/movie.model';
import { PageLoadingService } from '../../core/state/page-loading.service';
import { tmdbImage } from '../../core/utils/tmdb-image';
import { ComingSoonHintComponent } from '../../shared/coming-soon-hint/coming-soon-hint.component';

interface ActorOrbitItem {
  actor: MovieCastMember;
  x: number;
  y: number;
  side: 'left' | 'right';
}

@Component({
  selector: 'app-movie-page',
  standalone: true,
  imports: [RouterLink, ComingSoonHintComponent],
  templateUrl: './movie-page.component.html',
  styleUrl: './movie-page.component.scss'
})
export class MoviePageComponent {
  private readonly route = inject(ActivatedRoute);

  private readonly movieApi = inject(MovieApiService);

  private readonly pageLoading = inject(PageLoadingService);

  private readonly similarTrack =
    viewChild<ElementRef<HTMLDivElement>>('similarTrack');

  private readonly otherTrack =
    viewChild<ElementRef<HTMLDivElement>>('otherTrack');

  page = signal<MoviePage | null>(null);

  showAllCast = signal(false);

  readonly featuredCast = computed(() =>
    (this.page()?.cast ?? [])
      .filter(actor => !!actor.profilePath)
      .slice(0, 5)
  );

  readonly extraCast = computed(() => {
    const featuredIds = new Set(
      this.featuredCast().map(actor => actor.personId)
    );

    return (this.page()?.cast ?? []).filter(
      actor => actor.profilePath && !featuredIds.has(actor.personId)
    );
  });

  readonly orbitActors = computed(() => {
    const actors = this.featuredCast();

    return actors.map((actor, index) =>
      this.toOrbitItem(actor, index, actors.length)
    );
  });

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap(params => {
          const movieId = params.get('id');

          this.page.set(null);
          this.showAllCast.set(false);

          if (!movieId) {
            this.pageLoading.stop();
            return EMPTY;
          }

          this.pageLoading.start('Importing movie...');

          return this.movieApi.getMoviePage(movieId);
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

  profileUrl(path: string | null | undefined): string | null {
    return tmdbImage(path, 'w185');
  }

  backdropStyle(movie: MovieSummary): string {
    const url = tmdbImage(movie.backdropPath ?? movie.posterPath, 'original');

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

  scrollRow(kind: 'similar' | 'other', direction: -1 | 1): void {
    const track =
      kind === 'similar'
        ? this.similarTrack()?.nativeElement
        : this.otherTrack()?.nativeElement;

    track?.scrollBy({
      left: direction * 420,
      behavior: 'smooth'
    });
  }

  toggleCast(): void {
    this.showAllCast.update(value => !value);
  }

  private toOrbitItem(
    actor: MovieCastMember,
    index: number,
    total: number
  ): ActorOrbitItem {
    const layouts: Record<number, Array<Pick<ActorOrbitItem, 'x' | 'y' | 'side'>>> = {
      1: [{ x: 0, y: -220, side: 'right' }],
      2: [
        { x: -280, y: 10, side: 'left' },
        { x: 280, y: 10, side: 'right' }
      ],
      3: [
        { x: -280, y: -90, side: 'left' },
        { x: 280, y: -90, side: 'right' },
        { x: -290, y: 110, side: 'left' }
      ],
      4: [
        { x: -205, y: -110, side: 'left' },
        { x: 205, y: -110, side: 'right' },
        { x: -220, y: 90, side: 'left' },
        { x: 220, y: 90, side: 'right' }
      ],
      5: [
        { x: -205, y: -125, side: 'left' },
        { x: 205, y: -125, side: 'right' },
        { x: -225, y: 8, side: 'left' },
        { x: 225, y: 18, side: 'right' },
        { x: -200, y: 145, side: 'left' }
      ]
    };

    const layout = layouts[total] ?? layouts[5];
    const slot = layout[index] ?? layout[layout.length - 1];

    return {
      actor,
      ...slot
    };
  }
}
