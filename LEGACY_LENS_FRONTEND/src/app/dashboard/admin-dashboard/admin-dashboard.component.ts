// import { Component, inject, signal, computed } from '@angular/core';
// import { CommonModule } from '@angular/common';

// import { AuthService }             from '../../core/services/auth.service';
// import { ToastService }            from '../../core/services/toast.service';
// import { UserManagementComponent } from '../admin-user-management/admin-user-management.component';

// export type AdminSection = 'overview' | 'users' | 'audit' | 'settings';

// @Component({
//   selector: 'app-admin-dashboard',
//   standalone: true,
//   imports: [CommonModule, UserManagementComponent],
//   templateUrl: './admin-dashboard.component.html',
//   styleUrls: ['./admin-dashboard.component.css'],
// })
// export class AdminDashboardComponent {
//   private auth  = inject(AuthService);
//   private toast = inject(ToastService);

//   user          = computed(() => this.auth.getUser());
//   activeSection = signal<AdminSection>('overview');

//   adminStats = [
//     { label: 'Total Users',     value: '2,841', delta: '+142',   positive: true, icon: '👥' },
//     { label: 'Active Sessions', value: '312',   delta: '+28',    positive: true, icon: '⚡' },
//     { label: 'Repos Indexed',   value: '18.4k', delta: '+1.2k',  positive: true, icon: '⬡' },
//     { label: 'Error Rate',      value: '0.12%', delta: '-0.04%', positive: true, icon: '🛡' },
//   ];

//   auditLogs = [
//     { event: 'User Login',         actor: 'priya@acme.com',   time: '2m ago',  severity: 'info' },
//     { event: 'Repo Connected',     actor: 'james@corp.io',    time: '12m ago', severity: 'info' },
//     { event: 'Failed Login (3x)',  actor: 'unknown@mail.com', time: '1h ago',  severity: 'warn' },
//     { event: 'Admin Role Granted', actor: 'sofia@startup.ai', time: '1d ago',  severity: 'critical' },
//     { event: 'API Key Generated',  actor: 'marcus@bigco.com', time: '2d ago',  severity: 'info' },
//   ];

//   setSection(s: AdminSection): void { this.activeSection.set(s); }

//   onManageUser(publicId: string): void {
//     console.log('Manage user:', publicId);
//   }

//   logout(): void {
//     this.auth.logout();
//     this.toast.info('Signed out successfully.');
//   }
// }

import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { AdminUserService } from '../../core/services/admin.service';
import { UserManagementComponent } from '../admin-user-management/admin-user-management.component';

export type AdminSection = 'overview' | 'users' | 'audit' | 'settings';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, UserManagementComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {

  private auth = inject(AuthService);
  private toast = inject(ToastService);
  private adminService = inject(AdminUserService); 

  user = computed(() => this.auth.getUser());
  activeSection = signal<AdminSection>('overview');

  // ✅ NOW DYNAMIC (signal instead of static array)
  adminStats = signal([
    { label: 'Total Users', value: '0', delta: '', positive: true, icon: '👥' },
    { label: 'Active Sessions', value: '0', delta: '', positive: true, icon: '⚡' },
    { label: 'Repos Indexed', value: '0', delta: '', positive: true, icon: '⬡' },
    { label: 'Error Rate', value: '0%', delta: '', positive: true, icon: '🛡' },
  ]);

  auditLogs = [
    { event: 'User Login', actor: 'priya@acme.com', time: '2m ago', severity: 'info' },
    { event: 'Repo Connected', actor: 'james@corp.io', time: '12m ago', severity: 'info' },
    { event: 'Failed Login (3x)', actor: 'unknown@mail.com', time: '1h ago', severity: 'warn' },
    { event: 'Admin Role Granted', actor: 'sofia@startup.ai', time: '1d ago', severity: 'critical' },
    { event: 'API Key Generated', actor: 'marcus@bigco.com', time: '2d ago', severity: 'info' },
  ];

  // ── Lifecycle ─────────────────────────────
  ngOnInit(): void {
    this.loadStats();
  }

  // ── Load real stats from backend ──────────
  loadStats(): void {
    this.adminService.getStats().subscribe({
      next: (res) => {
        this.adminStats.set([
          {
            label: 'Total Users',
            value: res.totalUsers?.toString() || '0',
            delta: '',
            positive: true,
            icon: '👥',
          },
          {
            label: 'Active Sessions',
            value: res.activeSessions?.toString() || '0',
            delta: '',
            positive: true,
            icon: '⚡',
          },
          {
            label: 'Repos Indexed',
            value: res.reposIndexed?.toString() || '0',
            delta: '',
            positive: true,
            icon: '⬡',
          },
          {
            label: 'Error Rate',
            value: (res.errorRate ?? 0) + '%',
            delta: '',
            positive: true,
            icon: '🛡',
          },
        ]);
      },
      error: () => {
        this.toast.error('Failed to load dashboard stats');
      },
    });
  }

  // ── Navigation ───────────────────────────
  setSection(s: AdminSection): void {
    this.activeSection.set(s);
  }

  onManageUser(publicId: string): void {
    console.log('Manage user:', publicId);
  }

  // ── Logout ───────────────────────────────
  logout(): void {
    this.auth.logout();
    this.toast.info('Signed out successfully.');
  }
}