// import {
//   Component,
//   inject,
//   signal,
//   OnInit,
//   DestroyRef,
//   output,
// } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
// import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
// import { AdminUserService } from '../../core/services/admin.service';
// import { ToastService } from '../../core/services/toast.service';
// import { UserResponse } from '../../core/models/user.models';

// @Component({
//   selector: 'app-admin-user-management',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './admin-user-management.component.html',
//   styleUrls: ['./admin-user-management.component.css'],
// })
// export class UserManagementComponent implements OnInit {
//   private userService = inject(AdminUserService);
//   private toast = inject(ToastService);
//   private destroyRef = inject(DestroyRef);

//   // Emits when parent should navigate away (e.g. back to overview)
//   navigate = output<string>();

//   // ── State ─────────────────────────────────────────────
//   users = signal<UserResponse[]>([]);
//   isLoading = signal(false);
//   currentPage = signal(0);
//   totalPages = signal(0);
//   totalElements = signal(0);
//   pageSize = 10;

//   searchQuery = signal('');
//   private search$ = new Subject<string>();

//   // ── Lifecycle ─────────────────────────────────────────
//   ngOnInit(): void {
//     this.search$
//       .pipe(
//         debounceTime(350),
//         distinctUntilChanged(),
//         switchMap((q) => {
//           this.currentPage.set(0);
//           this.isLoading.set(true);
//           return this.userService.getUsers(0, this.pageSize, q);
//         }),
//         takeUntilDestroyed(this.destroyRef)
//       )
//       .subscribe({
//         next: (res) => {
//           this.users.set(res.content);
//           this.currentPage.set(res.page);
//           this.totalPages.set(res.totalPages);
//           this.totalElements.set(res.totalElements);
//           this.isLoading.set(false);
//         },
//         error: () => {
//           this.toast.error('Failed to search users.');
//           this.isLoading.set(false);
//         },
//       });

//     this.loadUsers(0);
//   }

//   // ── Data ──────────────────────────────────────────────
//   loadUsers(page: number): void {
//     this.isLoading.set(true);
//     this.userService
//       .getUsers(page, this.pageSize, this.searchQuery())
//       .pipe(takeUntilDestroyed(this.destroyRef))
//       .subscribe({
//         next: (res) => {
//           this.users.set(res.content);
//           this.currentPage.set(res.page);
//           this.totalPages.set(res.totalPages);
//           this.totalElements.set(res.totalElements);
//           this.isLoading.set(false);
//         },
//         error: () => {
//           this.toast.error('Failed to load users.');
//           this.isLoading.set(false);
//         },
//       });
//   }

//   exportCsv(): void {
//     if (this.isExporting()) return;
//     this.isExporting.set(true);

//     this.userService
//       .exportUsers(this.searchQuery())
//       .pipe(takeUntilDestroyed(this.destroyRef))
//       .subscribe({
//         next: (blob) => {
//           // Build a filename with today's date: users_export_2025-01-15.csv
//           const date = new Date().toISOString().split('T')[0];
//           const filename = `users_export_${date}.csv`;

//           // Create a temporary <a> and trigger browser download
//           const url = URL.createObjectURL(blob);
//           const link = document.createElement('a');
//           link.href = url;
//           link.download = filename;
//           link.click();
//           URL.revokeObjectURL(url);

//           this.isExporting.set(false);
//           this.toast.info(`Exported ${this.totalElements()} users.`);
//         },
//         error: () => {
//           this.toast.error('Export failed. Please try again.');
//           this.isExporting.set(false);
//         },
//       });
//   }

//   onSearchChange(value: string): void {
//     this.searchQuery.set(value);
//     this.search$.next(value);
//   }

//   goToPage(page: number): void {
//     if (page < 0 || page >= this.totalPages()) return;
//     this.currentPage.set(page);
//     this.loadUsers(page);
//   }

//   onManage(user: UserResponse): void {
//     // Emit publicId for parent to open detail panel/route
//     this.navigate.emit(user.publicId);
//   }

//   // ── Helpers ───────────────────────────────────────────
//   initials(name: string): string {
//     return name
//       .split(' ')
//       .slice(0, 2)
//       .map((w) => w.charAt(0).toUpperCase())
//       .join('');
//   }

//   relativeTime(isoDate: string): string {
//     const diff = Date.now() - new Date(isoDate).getTime();
//     const mins = Math.floor(diff / 60_000);
//     const hours = Math.floor(mins / 60);
//     const days = Math.floor(hours / 24);
//     if (mins < 60) return `${mins}m ago`;
//     if (hours < 24) return `${hours}h ago`;
//     return `${days}d ago`;
//   }

//   pages(): number[] {
//     return Array.from({ length: this.totalPages() }, (_, i) => i);
//   }
// }


import {
  Component,
  inject,
  signal,
  OnInit,
  DestroyRef,
  output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
import { AdminUserService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { UserResponse } from '../../core/models/user.models';

@Component({
  selector: 'app-admin-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-management.component.html',
  styleUrls: ['./admin-user-management.component.css'],
})
export class UserManagementComponent implements OnInit {
  private userService = inject(AdminUserService);
  private toast = inject(ToastService);
  private destroyRef = inject(DestroyRef);

  // Emits when parent should navigate away
  navigate = output<string>();

  // ── State ─────────────────────────────────────────────
  users = signal<UserResponse[]>([]);
  isLoading = signal(false);
  isExporting = signal(false); // ✅ FIXED

  currentPage = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  pageSize = 10;

  searchQuery = signal('');
  private search$ = new Subject<string>();

  // ── Lifecycle ─────────────────────────────────────────
  ngOnInit(): void {
    this.search$
      .pipe(
        debounceTime(350),
        distinctUntilChanged(),
        switchMap((q) => {
          this.currentPage.set(0);
          this.isLoading.set(true);
          return this.userService.getUsers(0, this.pageSize, q);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (res) => {
          this.users.set(res.content);
          this.currentPage.set(res.page);
          this.totalPages.set(res.totalPages);
          this.totalElements.set(res.totalElements);
          this.isLoading.set(false);
        },
        error: () => {
          this.toast.error('Failed to search users.');
          this.isLoading.set(false);
        },
      });

    this.loadUsers(0);
  }

  // ── Data ──────────────────────────────────────────────
  loadUsers(page: number): void {
    this.isLoading.set(true);
    this.userService
      .getUsers(page, this.pageSize, this.searchQuery())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.users.set(res.content);
          this.currentPage.set(res.page);
          this.totalPages.set(res.totalPages);
          this.totalElements.set(res.totalElements);
          this.isLoading.set(false);
        },
        error: () => {
          this.toast.error('Failed to load users.');
          this.isLoading.set(false);
        },
      });
  }

  exportCsv(): void {
    if (this.isExporting()) return;
    this.isExporting.set(true);

    this.userService
      .exportUsers(this.searchQuery())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blob) => {
          const date = new Date().toISOString().split('T')[0];
          const filename = `users_export_${date}.csv`;

          const url = URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = filename;
          link.click();
          URL.revokeObjectURL(url);

          this.isExporting.set(false);
          this.toast.info(`Exported ${this.totalElements()} users.`);
        },
        error: () => {
          this.toast.error('Export failed. Please try again.');
          this.isExporting.set(false);
        },
      });
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
    this.search$.next(value);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.loadUsers(page);
  }

  onManage(user: UserResponse): void {
    this.navigate.emit(user.publicId);
  }

  // ── Helpers ───────────────────────────────────────────
  initials(name: string): string {
    return name
      .split(' ')
      .slice(0, 2)
      .map((w) => w.charAt(0).toUpperCase())
      .join('');
  }

  relativeTime(isoDate: string): string {
    const diff = Date.now() - new Date(isoDate).getTime();
    const mins = Math.floor(diff / 60_000);
    const hours = Math.floor(mins / 60);
    const days = Math.floor(hours / 24);

    if (mins < 60) return `${mins}m ago`;
    if (hours < 24) return `${hours}h ago`;
    return `${days}d ago`;
  }

  pages(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }
}