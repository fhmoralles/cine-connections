import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, combineLatest, switchMap } from 'rxjs';

import { GraphApiService } from '../../core/api/graph-api.service';
import { PersonApiService } from '../../core/api/person-api.service';
import { GraphResponse } from '../../core/models/graph.model';
import { PageLoadingService } from '../../core/state/page-loading.service';
import { GraphExplorerComponent } from '../graph/graph-explorer/graph-explorer.component';

@Component({
  selector: 'app-person-page',
  standalone: true,
  imports: [GraphExplorerComponent],
  templateUrl: './person-page.component.html',
  styleUrl: './person-page.component.scss'
})
export class PersonPageComponent {
  private readonly route = inject(ActivatedRoute);

  private readonly graphApi = inject(GraphApiService);

  private readonly personApi = inject(PersonApiService);

  private readonly pageLoading = inject(PageLoadingService);

  graph = signal<GraphResponse | null>(null);

  constructor() {
    combineLatest([
      this.route.paramMap,
      this.route.queryParamMap
    ])
      .pipe(
        switchMap(([params, query]) => {
          const personId = params.get('id');
          const tmdbId = query.get('tmdbId');

          this.graph.set(null);

          if (!personId) {
            this.pageLoading.stop();
            return EMPTY;
          }

          if (tmdbId) {
            this.pageLoading.start('Importing filmography...');

            return this.personApi.importPerson(Number(tmdbId)).pipe(
              switchMap(person => {
                this.pageLoading.start('Building connections...');
                return this.graphApi.getPersonGraph(person.id, 2);
              })
            );
          }

          this.pageLoading.start('Building connections...');

          return this.graphApi.getPersonGraph(personId, 2);
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
