package com.legacy_lens.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AnalysisResponseDto {

    // Language detected (or confirmed) by the model
    private String detectedLanguage;

    // Confidence percentage string, e.g. "94%"
    private String confidence;


     //Keyed by analysis type (e.g. "EXPLAIN", "BUG_DETECTION").
     //Value is the raw markdown / plain-text response from Ollama.

    private Map<String, String> results;

    // Total time taken in milliseconds
    private long durationMs;

    // Number of lines in the submitted code
    private int lineCount;
}
