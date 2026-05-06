import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { StorageService } from './storage.service';
import { RegisterRequest, LoginRequest, OtpVerifyRequest, LoginResponse, AuthUser, ApiResponse } from '../models/auth.models';
import { map } from 'rxjs/operators';
import { environment } from '../../shared/environment/environment';

// const BASE = 'http://localhost:8086/legacylens/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private storage = inject(StorageService);
  private router = inject(Router);
  private base = `${environment.apiUrl}/auth`;

  register(payload: RegisterRequest): Observable<string> {
    return this.http.post<any>(`${this.base}/register`, payload).pipe(
      map(() => payload.email)
    );
  }

  // login(payload: LoginRequest): Observable<LoginResponse> {
  //   return this.http.post<LoginResponse>(`${BASE}/login`, payload).pipe(
  //     tap(res => {
  //       const user: AuthUser = {
  //         token: res.token,
  //         publicId: res.publicId,
  //         name: res.name,
  //         email: res.email,
  //         role: res.role,
  //       };
  //       this.storage.setToken(res.token);
  //       this.storage.setUser(user);
  //     })
  //   );
  // }

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.base}/login`, payload).pipe(
      map(res => res.data),           // ← unwrap the wrapper
      tap(res => {
        const user: AuthUser = {
          token: res.token,
          publicId: res.publicId,
          name: res.name,
          email: res.email,
          role: res.role,         
        };
        this.storage.setToken(res.token);
        this.storage.setUser(user);
      })
    );
  }

  verifyOtp(payload: OtpVerifyRequest): Observable<unknown> {
    return this.http.post(`${this.base}/verify-otp`, payload);
  }

  resendOtp(email: string): Observable<unknown> {
    return this.http.post(`${this.base}/resend-otp`, { email });
  }

  logout(): void {
    this.storage.clear();
    this.router.navigate(['/login']);
  }

  getToken(): string | null { return this.storage.getToken(); }
  getUser(): AuthUser | null { return this.storage.getUser(); }
  isAuthenticated(): boolean { return this.storage.isAuthenticated(); }
}
