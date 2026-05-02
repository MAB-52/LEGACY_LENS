import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="spinner" [class.spinner--sm]="size === 'sm'" [class.spinner--lg]="size === 'lg'"></span>
  `,
  styles: [`
    .spinner {
      display: inline-block;
      width: 1.25rem;
      height: 1.25rem;
      border: 2px solid rgba(255,255,255,.2);
      border-top-color: #fff;
      border-radius: 50%;
      animation: spin 700ms linear infinite;
    }
    .spinner--sm { width: 1rem; height: 1rem; }
    .spinner--lg { width: 1.75rem; height: 1.75rem; border-width: 3px; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `],
})
export class LoadingSpinnerComponent {
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
}
