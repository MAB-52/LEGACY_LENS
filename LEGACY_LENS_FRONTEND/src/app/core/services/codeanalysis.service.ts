// import { Injectable, inject } from '@angular/core';
// import { HttpClient, HttpErrorResponse } from '@angular/common/http';
// import { Observable, throwError } from 'rxjs';
// import { catchError, map } from 'rxjs/operators';
// import { AnalysisRequest, AnalysisOption, AnalysisResult } from '../models/analysis.models';
// import { environment } from '../../shared/environment/environment';

// export interface OllamaModel {
//   name: string;
//   modified_at: string;
//   size: number;
// }

// @Injectable({ providedIn: 'root' })
// export class CodeAnalysisService {
//   private http = inject(HttpClient);
//   private base = `${environment.apiUrl}/user/analysis`;

//   // Direct Ollama URL — same approach as AiContentGeneratorService
//   private readonly ollamaBase = 'http://localhost:11434';

//   readonly analysisOptions: AnalysisOption[] = [
//     {
//       key: 'EXPLAIN',
//       label: 'Plain English',
//       icon: '📖',
//       description: 'Clear, non-technical explanation of what this code does — ideal for stakeholders and documentation.',
//     },
//     {
//       key: 'LINE_BY_LINE',
//       label: 'Line-by-Line',
//       icon: '🔢',
//       description: 'Detailed walkthrough of every function and logic block with inline commentary.',
//     },
//     {
//       key: 'EXECUTION_FLOW',
//       label: 'Execution Flow',
//       icon: '🔄',
//       description: 'Trace how data moves through the code, entry points, branches, and exit conditions.',
//     },
//     {
//       key: 'BUG_DETECTION',
//       label: 'Bug Detection',
//       icon: '🐛',
//       description: 'AI-powered scan for security vulnerabilities, logic errors, and deprecated APIs.',
//     },
//     {
//       key: 'REFACTORING',
//       label: 'Refactoring',
//       icon: '✨',
//       description: 'Clean code recommendations, design patterns, and performance optimization tips.',
//     },
//     {
//       key: 'MODERNIZATION',
//       label: 'Modernization',
//       icon: '🚀',
//       description: 'Migration paths to modern frameworks, cloud-ready architecture, and tech debt analysis.',
//     },
//     {
//       key: 'DOCUMENTATION',
//       label: 'Documentation',
//       icon: '📝',
//       description: 'Auto-generate Javadoc / JSDoc, README content, and inline comments.',
//     },
//   ];

//   // ── Fetch models directly from Ollama (browser → localhost:11434) ──
//   getModels(): Observable<string[]> {
//     return this.http
//       .get<{ models: OllamaModel[] }>(`${this.ollamaBase}/api/tags`)
//       .pipe(
//         map(r => (r.models ?? []).map(m => m.name)),
//         catchError(this._handleError)
//       );
//   }

//   // ── Send analysis request to Spring Boot backend ──────────────
//   analyze(request: AnalysisRequest): Observable<AnalysisResult> {
//     return this.http
//       .post<AnalysisResult>(`${this.base}/code`, request)
//       .pipe(catchError(this._handleError));
//   }

//   private _handleError(err: HttpErrorResponse): Observable<never> {
//     const msg =
//       err.status === 0
//         ? 'Cannot reach Ollama. Make sure it is running on localhost:11434.'
//         : `Server error ${err.status}: ${err.statusText}`;
//     return throwError(() => new Error(msg));
//   }
// }

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { AnalysisRequest, AnalysisOption, AnalysisResult } from '../models/analysis.models';
import { environment } from '../../shared/environment/environment';

export interface OllamaModel {
  name: string;
  modified_at: string;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class CodeAnalysisService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/user/analysis`;
  private readonly ollamaBase = 'http://localhost:11434';

  readonly analysisOptions: AnalysisOption[] = [
    { key: 'EXPLAIN',        label: 'Plain English',   icon: '📖', description: 'Clear, non-technical explanation of what this code does — ideal for stakeholders and documentation.' },
    { key: 'LINE_BY_LINE',   label: 'Line-by-Line',    icon: '🔢', description: 'Detailed walkthrough of every function and logic block with inline commentary.' },
    { key: 'EXECUTION_FLOW', label: 'Execution Flow',  icon: '🔄', description: 'Trace how data moves through the code, entry points, branches, and exit conditions.' },
    { key: 'BUG_DETECTION',  label: 'Bug Detection',   icon: '🐛', description: 'AI-powered scan for security vulnerabilities, logic errors, and deprecated APIs.' },
    { key: 'REFACTORING',    label: 'Refactoring',     icon: '✨', description: 'Clean code recommendations, design patterns, and performance optimization tips.' },
    { key: 'MODERNIZATION',  label: 'Modernization',   icon: '🚀', description: 'Migration paths to modern frameworks, cloud-ready architecture, and tech debt analysis.' },
    { key: 'DOCUMENTATION',  label: 'Documentation',   icon: '📝', description: 'Auto-generate Javadoc / JSDoc, README content, and inline comments.' },
  ];

  // ── Fetch models directly from Ollama ─────────────────────────
  getModels(): Observable<string[]> {
    return this.http
      .get<{ models: OllamaModel[] }>(`${this.ollamaBase}/api/tags`)
      .pipe(
        map(r => (r.models ?? []).map(m => m.name)),
        catchError(this._handleError)
      );
  }

  // ── Analyze code, then unload model from memory when done ─────
  analyze(request: AnalysisRequest): Observable<AnalysisResult> {
    return this.http
      .post<AnalysisResult>(`${this.base}/code`, request)
      .pipe(
        // After analysis result arrives, unload the selected model from memory
        switchMap(result => {
          const modelToUnload = request.model;

          if (!modelToUnload) {
            // No specific model was selected, nothing to unload
            return [result];
          }

          // Unload model by setting keep_alive: 0 — same pattern as AiContentGeneratorService
          return this.http
            .post<unknown>(`${this.ollamaBase}/api/generate`, {
              model:      modelToUnload,
              prompt:     '',
              keep_alive: 0,
            })
            .pipe(
              map(() => result),        // unload succeeded — pass original result through
              catchError(() => [result]) // unload failed silently — still return result
            );
        }),
        catchError(this._handleError)
      );
  }

  private _handleError(err: HttpErrorResponse): Observable<never> {
    const msg =
      err.status === 0
        ? 'Cannot reach Ollama. Make sure it is running on localhost:11434.'
        : `Server error ${err.status}: ${err.statusText}`;
    return throwError(() => new Error(msg));
  }
}