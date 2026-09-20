import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

import { environment } from '../../../environments/environment';

type Gtag = {
  (command: 'js', date: Date): void;
  (command: 'config', targetId: string, config?: Record<string, unknown>): void;
  (command: 'event', name: string, params?: Record<string, unknown>): void;
};

declare global {
  interface Window {
    dataLayer: unknown[];
    gtag: Gtag;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly platformId = inject(PLATFORM_ID);

  private readonly router = inject(Router);

  private readonly measurementId = environment.gaMeasurementId.trim();

  private started = false;

  init(): void {
    if (this.started || !this.measurementId || !isPlatformBrowser(this.platformId)) {
      return;
    }

    this.started = true;
    this.installGtag();

    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(event => this.pageView(event.urlAfterRedirects));

    this.pageView(this.router.url);
  }

  private installGtag(): void {
    window.dataLayer = window.dataLayer ?? [];
    window.gtag = function gtag() {
      window.dataLayer.push(arguments);
    } as Gtag;

    window.gtag('js', new Date());
    window.gtag('config', this.measurementId, {
      send_page_view: false,
      anonymize_ip: true
    });

    const script = document.createElement('script');
    script.async = true;
    script.src = `https://www.googletagmanager.com/gtag/js?id=${encodeURIComponent(this.measurementId)}`;
    document.head.appendChild(script);
  }

  private lastPath = '';

  private pageView(path: string): void {
    const pagePath = path || '/';

    if (pagePath === this.lastPath) {
      return;
    }

    this.lastPath = pagePath;

    window.gtag('event', 'page_view', {
      page_title: document.title,
      page_location: window.location.href,
      page_path: pagePath
    });
  }
}
