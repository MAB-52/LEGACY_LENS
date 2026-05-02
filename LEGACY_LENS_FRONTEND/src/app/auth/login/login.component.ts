import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LoadingSpinnerComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  private fb     = inject(FormBuilder);
  private auth   = inject(AuthService);
  private router = inject(Router);
  private toast  = inject(ToastService);

  loading         = signal(false);
  showPw          = signal(false);
  notVerifiedEmail = signal<string | null>(null);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  get f() { return this.form.controls; }

  togglePw() { this.showPw.update(v => !v); }

  goVerify(): void {
    const email = this.notVerifiedEmail();
    if (email) {
      this.router.navigate(['/verify-email'], { queryParams: { email } });
    }
  }

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.notVerifiedEmail.set(null);

    this.auth.login(this.form.value as any).subscribe({
      next: (res) => {
        this.toast.success(`Welcome back, ${res.name}!`);
        if (res.role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        const msg: string = err?.error?.message ?? err?.message ?? 'Login failed.';
        if (msg.toLowerCase().includes('verify')) {
          this.notVerifiedEmail.set(this.form.value.email ?? null);
          this.toast.warning('Please verify your email before signing in.');
        } else {
          this.toast.error(msg);
        }
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }
}
