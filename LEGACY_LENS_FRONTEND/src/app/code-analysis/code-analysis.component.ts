import {
  Component, inject, signal, computed, OnInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CodeAnalysisService } from '../core/services/codeanalysis.service';
import { AnalysisOption, AnalysisRequest, AnalysisResult } from '../core/models/analysis.models';

type Step = 'upload' | 'options' | 'processing' | 'results';

@Component({
  selector: 'app-code-analysis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './code-analysis.component.html',
  styleUrls: ['./code-analysis.component.css'],
})
export class CodeAnalysisComponent implements OnInit {
  private svc = inject(CodeAnalysisService);
  private router = inject(Router);

  // ── Ollama models ──────────────────────────────────────────────
  availableModels = signal<string[]>([]);
  selectedModel = signal<string>('');
  modelsLoading = signal(true);

  // ── wizard state ──────────────────────────────────────────────
  step = signal<Step>('upload');

  // ── upload / code paste state ─────────────────────────────────
  code = signal('');
  pastedCode = '';          // ngModel two-way binding target
  detectedLang = signal('Detecting...');
  confidence = signal(0);

  // ── analysis options ──────────────────────────────────────────
  options: AnalysisOption[] = [];
  selectedKeys = signal<Set<string>>(new Set(['EXPLAIN', 'BUG_DETECTION', 'MODERNIZATION']));

  selectedCount = computed(() => this.selectedKeys().size);

  // ── processing ────────────────────────────────────────────────
  processingSteps = [
    { id: 1, icon: '📥', label: 'Code uploaded & validated', status: signal<'queued' | 'active' | 'done'>('queued') },
    { id: 2, icon: '🔍', label: 'Language detection', status: signal<'queued' | 'active' | 'done'>('queued') },
    { id: 3, icon: '🧠', label: 'Building prompts for Ollama', status: signal<'queued' | 'active' | 'done'>('queued') },
    { id: 4, icon: '⚡', label: 'Running model analyses', status: signal<'queued' | 'active' | 'done'>('queued') },
    { id: 5, icon: '📊', label: 'Aggregating & formatting results', status: signal<'queued' | 'active' | 'done'>('queued') },
  ];
  progressPct = signal(0);

  // ── results ───────────────────────────────────────────────────
  result = signal<AnalysisResult | null>(null);
  activeTab = signal<string>('');
  error = signal<string | null>(null);

  // ── line count helper ─────────────────────────────────────────
  lineCount = computed(() => this.code().split('\n').length);

  ngOnInit() {
    this.options = this.svc.analysisOptions;

    // Fetch available Ollama models
    this.svc.getModels().subscribe({
      next: (models) => {
        this.availableModels.set(models);
        if (models.length) this.selectedModel.set(models[0]); // default to first
        this.modelsLoading.set(false);
      },
      error: () => {
        this.modelsLoading.set(false);
      }
    });
  }

  // ── step: upload ──────────────────────────────────────────────
  onCodeInput(value: string) {
    this.code.set(value);
    this.autoDetectLanguage(value);
  }

  private autoDetectLanguage(code: string) {
    const lower = code.toLowerCase();
    if (lower.includes('public class') || lower.includes('import java')) {
      this.detectedLang.set('Java'); this.confidence.set(94);
    } else if (lower.includes('def ') || lower.includes('import numpy') || lower.includes('print(')) {
      this.detectedLang.set('Python'); this.confidence.set(96);
    } else if (lower.includes('function ') || lower.includes('const ') || lower.includes('var ')) {
      this.detectedLang.set('JavaScript'); this.confidence.set(88);
    } else if (lower.includes('identification division')) {
      this.detectedLang.set('COBOL'); this.confidence.set(99);
    } else if (lower.includes('select ') && lower.includes('from ')) {
      this.detectedLang.set('SQL'); this.confidence.set(97);
    } else if (lower.includes('#include') || lower.includes('std::')) {
      this.detectedLang.set('C++'); this.confidence.set(92);
    } else if (code.trim().length > 20) {
      this.detectedLang.set('Unknown'); this.confidence.set(0);
    } else {
      this.detectedLang.set('Detecting...'); this.confidence.set(0);
    }
  }

  goToOptions() {
    if (!this.code().trim()) return;
    this.step.set('options');
  }

  // ── step: options ─────────────────────────────────────────────
  toggleOption(key: string) {
    const s = new Set(this.selectedKeys());
    s.has(key) ? s.delete(key) : s.add(key);
    this.selectedKeys.set(s);
  }

  isSelected(key: string) {
    return this.selectedKeys().has(key);
  }

  runAnalysis() {
    if (this.selectedKeys().size === 0) return;
    this.step.set('processing');
    this.error.set(null);
    this.startProcessingAnimation();

    this.svc.analyze({
      code: this.code(),
      analysisTypes: [...this.selectedKeys()],
      model: this.selectedModel() || undefined,
    }).subscribe({
      next: (res) => {
        this.result.set(res);
        this.finishProcessing(res);
      },
      error: (err) => {
        this.error.set(
          err?.error?.message ?? 'Could not connect to the analysis server. Is Ollama running?'
        );
        this.step.set('results');
        this.progressPct.set(0);
      },
    });
  }

  // ── processing animation ──────────────────────────────────────
  private startProcessingAnimation() {
    this.processingSteps.forEach(s => s.status.set('queued'));
    this.progressPct.set(0);

    // Advance through steps 1–3 with fake timing;
    // step 4 is held until the API resolves.
    const fakeDelays = [300, 800, 1500];
    fakeDelays.forEach((delay, i) => {
      setTimeout(() => {
        if (i > 0) this.processingSteps[i - 1].status.set('done');
        this.processingSteps[i].status.set('active');
        this.progressPct.set(20 + i * 18);
      }, delay);
    });
  }

  private finishProcessing(res: AnalysisResult) {
    // Mark step 3 done, start step 4
    this.processingSteps[2].status.set('done');
    this.processingSteps[3].status.set('active');
    this.progressPct.set(82);

    setTimeout(() => {
      this.processingSteps[3].status.set('done');
      this.processingSteps[4].status.set('active');
      this.progressPct.set(95);
      setTimeout(() => {
        this.processingSteps[4].status.set('done');
        this.progressPct.set(100);
        // Set first tab
        const keys = Object.keys(res.results);
        if (keys.length) this.activeTab.set(keys[0]);
        setTimeout(() => this.step.set('results'), 500);
      }, 600);
    }, 500);
  }

  // ── results ───────────────────────────────────────────────────
  resultKeys = computed(() => {
    const r = this.result();
    return r ? Object.keys(r.results) : [];
  });

  tabLabel(key: string): string {
    return this.options.find(o => o.key === key)?.label ?? key;
  }

  tabIcon(key: string): string {
    return this.options.find(o => o.key === key)?.icon ?? '📄';
  }

  activeResult = computed(() => {
    const r = this.result();
    if (!r) return '';
    return r.results[this.activeTab()] ?? '';
  });

  // ── utility ───────────────────────────────────────────────────
  backToDashboard() { this.router.navigate(['/dashboard']); }
  startOver() { this.step.set('upload'); this.result.set(null); this.error.set(null); }
  copyResult() { navigator.clipboard.writeText(this.activeResult()); }

  formatDuration(ms: number): string {
    return ms >= 1000 ? (ms / 1000).toFixed(1) + 's' : ms + 'ms';
  }
}