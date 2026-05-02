import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css'],
})
export class UserDashboardComponent {
  private auth   = inject(AuthService);
  private router = inject(Router);
  private toast  = inject(ToastService);

  user    = computed(() => this.auth.getUser());
  sideNav = signal<string>('overview');

  stats = [
    { label: 'Repos Analyzed',    value: '12',   unit: '',  delta: '+3',  positive: true,  icon: '⬡' },
    { label: 'Issues Detected',   value: '847',  unit: '',  delta: '+124',positive: false, icon: '⚠' },
    { label: 'Files Scanned',     value: '3.2k', unit: '',  delta: '+580',positive: true,  icon: '📄' },
    { label: 'AI Suggestions',    value: '291',  unit: '',  delta: '+47', positive: true,  icon: '✦' },
  ];

  recent = [
    { name: 'legacy-core',    lang: 'Java',       issues: 142, time: '2h ago',  status: 'critical' },
    { name: 'payments-api',   lang: 'Python',     issues:  38, time: '5h ago',  status: 'warning'  },
    { name: 'auth-service',   lang: 'Node.js',    issues:   7, time: '1d ago',  status: 'good'     },
    { name: 'data-pipeline',  lang: 'Go',         issues:  21, time: '2d ago',  status: 'warning'  },
    { name: 'report-engine',  lang: 'Kotlin',     issues:   0, time: '3d ago',  status: 'good'     },
  ];

  logout(): void {
    this.auth.logout();
    this.toast.info('You have been signed out.');
  }
}
