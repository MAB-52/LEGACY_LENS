import {
  Component, inject, signal, OnInit, OnDestroy,
  ViewChildren, QueryList, ElementRef, AfterViewInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

const OTP_TTL_SECONDS = 300; // 5 minutes

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LoadingSpinnerComponent],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css'],
})
export class VerifyEmailComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChildren('otpInput') otpInputs!: QueryList<ElementRef<HTMLInputElement>>;

  private auth    = inject(AuthService);
  private router  = inject(Router);
  private route   = inject(ActivatedRoute);
  private toast   = inject(ToastService);

  email      = signal('');
  digits     = signal<string[]>(['', '', '', '', '', '']);
  loading    = signal(false);
  resending  = signal(false);
  success    = signal(false);

  /** Seconds left until OTP expires */
  countdown  = signal(OTP_TTL_SECONDS);
  /** Whether the user can request a new OTP (countdown reached 0) */
  canResend  = signal(false);

  private countdownInterval: ReturnType<typeof setInterval> | null = null;

  // Lifecycle

  ngOnInit(): void {
    const emailParam = this.route.snapshot.queryParamMap.get('email') ?? '';
    this.email.set(emailParam);
    this.startCountdown();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.focusInput(0), 100);
  }

  ngOnDestroy(): void {
    this.clearCountdown();
  }

  // Countdown helpers 

  private startCountdown(): void {
    this.clearCountdown();
    this.countdown.set(OTP_TTL_SECONDS);
    this.canResend.set(false);

    this.countdownInterval = setInterval(() => {
      const next = this.countdown() - 1;
      if (next <= 0) {
        this.countdown.set(0);
        this.canResend.set(true);
        this.clearCountdown();
      } else {
        this.countdown.set(next);
      }
    }, 1000);
  }

  private clearCountdown(): void {
    if (this.countdownInterval !== null) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }

  /** Format seconds as mm:ss */
  get countdownLabel(): string {
    const s   = this.countdown();
    const min = Math.floor(s / 60).toString().padStart(2, '0');
    const sec = (s % 60).toString().padStart(2, '0');
    return `${min}:${sec}`;
  }

  // OTP input handling 

  private focusInput(index: number): void {
    this.otpInputs.toArray()[index]?.nativeElement.focus();
  }

  onInput(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const val   = input.value.replace(/\D/g, '').slice(-1);
    input.value = val;

    const arr = [...this.digits()];
    arr[index] = val;
    this.digits.set(arr);

    if (val && index < 5) this.focusInput(index + 1);
    if (this.digits().every(d => d !== '')) this.submit();
  }

  onKeydown(index: number, event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      const arr = [...this.digits()];
      if (arr[index]) {
        arr[index] = '';
        this.digits.set(arr);
      } else if (index > 0) {
        arr[index - 1] = '';
        this.digits.set(arr);
        this.focusInput(index - 1);
      }
    }
  }

  onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const text = event.clipboardData?.getData('text') ?? '';
    const nums  = text.replace(/\D/g, '').slice(0, 6).split('');
    const arr   = ['', '', '', '', '', ''];
    nums.forEach((n, i) => { arr[i] = n; });
    this.digits.set(arr);
    this.focusInput(Math.min(nums.length, 5));
    if (arr.every(d => d !== '')) this.submit();
  }

  get otpCode(): string { return this.digits().join(''); }

  // Actions 

  submit(): void {
    if (this.otpCode.length < 6 || this.loading()) return;
    this.loading.set(true);

    this.auth.verifyOtp({ email: this.email(), otpCode: this.otpCode }).subscribe({
      next: () => {
        this.success.set(true);
        this.clearCountdown();
        this.toast.success('Email verified! You can now sign in.');
        setTimeout(() => this.router.navigate(['/login']), 1800);
      },
      error: (err) => {
        const msg = err?.error?.message ?? 'Invalid or expired OTP.';
        this.toast.error(msg);
        this.digits.set(['', '', '', '', '', '']);
        this.loading.set(false);
        setTimeout(() => this.focusInput(0), 50);
      },
      complete: () => this.loading.set(false),
    });
  }

  resendOtp(): void {
    if (!this.canResend() || this.resending()) return;
    this.resending.set(true);

    this.auth.resendOtp(this.email()).subscribe({
      next: () => {
        this.toast.success('A new OTP has been sent to your inbox.');
        this.digits.set(['', '', '', '', '', '']);
        this.startCountdown();          // reset the 5-minute timer
        setTimeout(() => this.focusInput(0), 50);
      },
      error: (err) => {
        const msg = err?.error?.message ?? 'Could not resend OTP. Try again later.';
        this.toast.error(msg);
      },
      complete: () => this.resending.set(false),
    });
  }
}