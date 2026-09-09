import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AppHeaderComponent } from './layout/app-header/app-header.component';
import { PageLoadingService } from './core/state/page-loading.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AppHeaderComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  readonly pageLoading = inject(PageLoadingService);
}
