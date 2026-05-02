import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { StorageService } from '../services/storage.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(StorageService);
  const router = inject(Router);
  const token = storage.getToken();

  const cloned = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      // if (err.status === 401) {
      //   storage.clear();
      //   router.navigate(['/login']);
      // }
      // return throwError(() => err);

      if (err.status === 401 || err.status === 403) {
        storage.clear();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
