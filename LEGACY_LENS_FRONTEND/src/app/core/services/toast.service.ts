import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id: string;
  type: ToastType;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);

  private add(type: ToastType, message: string, duration = 4000): void {
    const id = crypto.randomUUID();
    this.toasts.update(list => [...list, { id, type, message }]);
    setTimeout(() => this.remove(id), duration);
  }

  remove(id: string): void {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }

  success(message: string) { this.add('success', message); }
  error(message: string)   { this.add('error',   message); }
  info(message: string)    { this.add('info',     message); }
  warning(message: string) { this.add('warning',  message); }
}
