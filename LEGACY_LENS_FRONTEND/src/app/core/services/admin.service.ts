import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedResponse, UserResponse } from '../models/user.models';
import { environment } from '../../shared/environment/environment';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
    private http = inject(HttpClient);
    private base = `${environment.apiUrl}/admin/users`;

    // Get all users...
    getUsers(
        page = 0,
        size = 10,
        search?: string
    ): Observable<PagedResponse<UserResponse>> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        if (search && search.trim()) {
            params = params.set('search', search.trim());
        }

        return this.http.get<PagedResponse<UserResponse>>(this.base, { params });
    }

    // Export all users in CSV format...
    exportUsers(search?: string): Observable<Blob> {
        let params = new HttpParams();

        if (search?.trim()) {
            params = params.set('search', search.trim());
        }

        return this.http.get(`${this.base}/export`, {
            params,
            responseType: 'blob',   // tells HttpClient to treat response as binary
        });
    }

    // ── Dashboard stats ─────────────────────
    getStats(): Observable<{
        totalUsers: number;
        activeSessions: number;
        reposIndexed: number;
        errorRate: number;
    }> {
        return this.http.get<{
            totalUsers: number;
            activeSessions: number;
            reposIndexed: number;
            errorRate: number;
        }>(`${this.base}/stats`);
    }
}