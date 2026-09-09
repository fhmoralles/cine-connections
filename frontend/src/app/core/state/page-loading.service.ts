import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PageLoadingService {
  readonly loading = signal(false);

  readonly message = signal('Building connections...');

  start(message: string): void {
    this.message.set(message);
    this.loading.set(true);
  }

  stop(): void {
    this.loading.set(false);
  }
}
