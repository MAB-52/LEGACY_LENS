import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [

  //Default route
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  //Authentication routes
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then(m => m.RegisterComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'verify-email',
    loadComponent: () =>
      import('./auth/verify-email/verify-email.component').then(m => m.VerifyEmailComponent),
  },

  //User Dashboard
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./dashboard/user-dashboard/user-dashboard.component').then(m => m.UserDashboardComponent),
  },

  //Admin dashboard
  {
    path: 'admin-dashboard',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./dashboard/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
  },

  // Code Analysis Page
  {
    path: 'code-analysis',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./code-analysis/code-analysis.component')
        .then(m => m.CodeAnalysisComponent),
  },

  //Wildcard Route
  { path: '**', redirectTo: 'login' },
];
