import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, switchMap } from 'rxjs';

import { GraphApiService } from '../../core/api/graph-api.service';
import { GraphResponse } from '../../core/models/graph.model';
import { PageLoadingService } from '../../core/state/page-loading.service';
import { GraphExplorerComponent } from '../graph/graph-explorer/graph-explorer.component';

@Component({
  selector: 'app-movie-page',
  standalone: true,
  imports: [GraphExplorerComponent],
  templateUrl: './movie-page.component.html',
  styleUrl: './movie-page.component.scss'
})
export class MoviePageComponent {
  private readonly route = inject(ActivatedRoute);

  private readonly graphApi = inject(GraphApiService);

  private readonly pageLoading = inject(PageLoadingService);

  graph = signal<GraphResponse | null>(null);

  constructor() {
    this.route.paramMap
      .pipe(
        switchMap(params => {
          const movieId = params.get('id');

          this.graph.set(null);

          if (!movieId) {
            this.pageLoading.stop();
            return EMPTY;
          }

          this.pageLoading.start('Building movie connections...');

          return this.graphApi.getMovieGraph(movieId, 1);
        }),
        takeUntilDestroyed()
      )
      .subscribe({
        next: graph => {
          this.graph.set(graph);
          this.pageLoading.stop();
        },
        error: () => {
          this.graph.set(null);
          this.pageLoading.stop();
        }
      });
  }
}
