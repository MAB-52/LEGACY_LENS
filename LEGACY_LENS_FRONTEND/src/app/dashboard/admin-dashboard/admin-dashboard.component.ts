// import { Component, inject, signal, computed } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { AuthService } from '../../core/services/auth.service';
// import { ToastService } from '../../core/services/toast.service';

// type AdminSection = 'overview' | 'users' | 'audit' | 'settings';

// @Component({
//   selector: 'app-admin-dashboard',
//   standalone: true,
//   imports: [CommonModule],
//   templateUrl: './admin-dashboard.component.html',
//   styleUrls: ['./admin-dashboard.component.css'],
// })
// export class AdminDashboardComponent {
//   private auth  = inject(AuthService);
//   private toast = inject(ToastService);

//   user           = computed(() => this.auth.getUser());
//   activeSection  = signal<AdminSection>('overview');

//   adminStats = [
//     { label: 'Total Users',     value: '2,841', delta: '+142', positive: true,  icon: '👥' },
//     { label: 'Active Sessions', value: '312',   delta: '+28',  positive: true,  icon: '⚡' },
//     { label: 'Repos Indexed',   value: '18.4k', delta: '+1.2k',positive: true,  icon: '⬡' },
//     { label: 'Error Rate',      value: '0.12%', delta: '-0.04%',positive: true, icon: '🛡' },
//   ];

//   recentUsers = [
//     { name: 'Priya Sharma',   email: 'priya@acme.com',    role: 'USER',  status: 'active',   joined: '2h ago' },
//     { name: 'James Wilson',   email: 'james@corp.io',     role: 'USER',  status: 'active',   joined: '4h ago' },
//     { name: 'Sofia Chen',     email: 'sofia@startup.ai',  role: 'ADMIN', status: 'active',   joined: '1d ago' },
//     { name: 'Marcus Lee',     email: 'marcus@bigco.com',  role: 'USER',  status: 'inactive', joined: '2d ago' },
//     { name: 'Nadia Hassan',   email: 'nadia@labs.dev',    role: 'USER',  status: 'active',   joined: '3d ago' },
//   ];

//   auditLogs = [
//     { event: 'User Login',         actor: 'priya@acme.com',    time: '2m ago',  severity: 'info' },
//     { event: 'Repo Connected',      actor: 'james@corp.io',     time: '12m ago', severity: 'info' },
//     { event: 'Failed Login (3×)',   actor: 'unknown@mail.com',  time: '1h ago',  severity: 'warn' },
//     { event: 'Admin Role Granted',  actor: 'sofia@startup.ai',  time: '1d ago',  severity: 'critical' },
//     { event: 'API Key Generated',   actor: 'marcus@bigco.com',  time: '2d ago',  severity: 'info' },
//   ];

//   setSection(s: AdminSection): void { this.activeSection.set(s); }

//   logout(): void {
//     this.auth.logout();
//     this.toast.info('Signed out successfully.');
//   }
// }

import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService }             from '../../core/services/auth.service';
import { ToastService }            from '../../core/services/toast.service';
import { UserManagementComponent } from '../admin-user-management/admin-user-management.component';

export type AdminSection = 'overview' | 'users' | 'audit' | 'settings';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, UserManagementComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent {
  private auth  = inject(AuthService);
  private toast = inject(ToastService);

  user          = computed(() => this.auth.getUser());
  activeSection = signal<AdminSection>('overview');

  adminStats = [
    { label: 'Total Users',     value: '2,841', delta: '+142',   positive: true, icon: '👥' },
    { label: 'Active Sessions', value: '312',   delta: '+28',    positive: true, icon: '⚡' },
    { label: 'Repos Indexed',   value: '18.4k', delta: '+1.2k',  positive: true, icon: '⬡' },
    { label: 'Error Rate',      value: '0.12%', delta: '-0.04%', positive: true, icon: '🛡' },
  ];

  auditLogs = [
    { event: 'User Login',         actor: 'priya@acme.com',   time: '2m ago',  severity: 'info' },
    { event: 'Repo Connected',     actor: 'james@corp.io',    time: '12m ago', severity: 'info' },
    { event: 'Failed Login (3x)',  actor: 'unknown@mail.com', time: '1h ago',  severity: 'warn' },
    { event: 'Admin Role Granted', actor: 'sofia@startup.ai', time: '1d ago',  severity: 'critical' },
    { event: 'API Key Generated',  actor: 'marcus@bigco.com', time: '2d ago',  severity: 'info' },
  ];

  setSection(s: AdminSection): void { this.activeSection.set(s); }

  onManageUser(publicId: string): void {
    console.log('Manage user:', publicId);
  }

  logout(): void {
    this.auth.logout();
    this.toast.info('Signed out successfully.');
  }
}