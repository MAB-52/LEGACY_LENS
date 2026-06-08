package com.legacy_lens.controller;

import com.legacy_lens.dto.request.AnalysisRequestDto;
import com.legacy_lens.dto.response.AnalysisResponseDto;
import com.legacy_lens.service.CodeAnalysisService;
import com.legacy_lens.service.OllamaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legacylens/user/analysis")
@RequiredArgsConstructor

public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;
    private final OllamaService ollamaService;


    //Run one or more analysis types on the submitted code.
    @PostMapping("/code")
    public ResponseEntity<AnalysisResponseDto> analyzeCode(
            @Valid @RequestBody AnalysisRequestDto request) {

        AnalysisResponseDto result = codeAnalysisService.analyze(request);
        return ResponseEntity.ok(result);
    }

    //Returns the list of supported analysis types so the frontend can build the options grid dynamically.
    @GetMapping("/types")
    public ResponseEntity<List<String>> getSupportedTypes() {
        return ResponseEntity.ok(codeAnalysisService.getSupportedAnalysisTypes());
    }
    
    // Get ollama models
    @GetMapping("/models")
    public ResponseEntity<List<String>> getAvailableModels() {
        return ResponseEntity.ok(ollamaService.getAvailableModels());
    }
}