import { Routes } from '@angular/router';

import { SearchComponent } from './features/search/search.component';
import { MoviePageComponent } from './features/movie/movie-page.component';
import { PersonPageComponent } from './features/person/person-page.component';

export const routes: Routes = [
  {
    path: '',
    component: SearchComponent
  },
  {
    path: 'movie/:id',
    component: MoviePageComponent
  },
  {
    path: 'person/:id',
    component: PersonPageComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
