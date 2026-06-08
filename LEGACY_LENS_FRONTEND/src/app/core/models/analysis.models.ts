export interface AnalysisRequest {
  code: string;
  language?: string;
  analysisTypes: string[];
  model?: string;
}

export interface AnalysisResult {
  detectedLanguage: string;
  confidence: string;
  results: Record<string, string>;
  durationMs: number;
  lineCount: number;
}

export interface AnalysisOption {
  key: string;
  label: string;
  icon: string;
  description: string;
}