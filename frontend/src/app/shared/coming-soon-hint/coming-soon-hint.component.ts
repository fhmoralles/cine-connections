import { Component } from '@angular/core';

@Component({
  selector: 'app-coming-soon-hint',
  standalone: true,
  template: `<p>Coming soon. Help us improve...</p>`,
  styles: `
    :host {
      display: block;
    }

    p {
      margin: 0;
      color: #64748b;
      font-size: 13px;
      line-height: 1.55;
    }

    :host(.empty-row) p {
      margin: 18px 8px;
    }
  `
})
export class ComingSoonHintComponent {}
