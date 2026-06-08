package com.legacy_lens.service;

import com.legacy_lens.dto.request.AnalysisRequestDto;
import com.legacy_lens.dto.response.AnalysisResponseDto;

import java.util.List;

public interface CodeAnalysisService {

    AnalysisResponseDto analyze(AnalysisRequestDto request);
    String detectLanguage(String code);
    String buildPrompt(String type, String code, String language);
    String explainPrompt(String code, String lang);
    String lineByLinePrompt(String code, String lang);
    String bugDetectionPrompt(String code, String lang);
    String modernizationPrompt(String code, String lang);
    String refactoringPrompt(String code, String lang);
    String documentationPrompt(String code, String lang);
    String executionFlowPrompt(String code, String lang);
    String genericPrompt(String type, String code, String lang);
    List<String> getSupportedAnalysisTypes();
}
