package com.legacy_lens.service.impl;

import com.legacy_lens.dto.request.AnalysisRequestDto;
import com.legacy_lens.dto.response.AnalysisResponseDto;
import com.legacy_lens.service.CodeAnalysisService;
import com.legacy_lens.service.OllamaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeAnalysisServiceImpl implements CodeAnalysisService {

    private final OllamaService ollamaService;

    // Public API
//    public AnalysisResponseDto analyze(AnalysisRequestDto request) {
//        long start = System.currentTimeMillis();
//
//        String code = request.getCode();
//        int lineCount = code.split("\n", -1).length;
//
//        // 1. Detect language if not supplied
//        String language = (request.getLanguage() != null && !request.getLanguage().isBlank())
//                ? request.getLanguage()
//                : detectLanguage(code);
//
//        // 2. Run each requested analysis
//        Map<String, String> results = new LinkedHashMap<>();
//        for (String type : request.getAnalysisTypes()) {
//            log.info("Running analysis type '{}' for detected language '{}'", type, language);
//            String prompt = buildPrompt(type, code, language);
//            String result = ollamaService.generate(prompt);
//            results.put(type, result);
//        }
//
//        long duration = System.currentTimeMillis() - start;
//        log.info("Analysis complete in {}ms, types={}", duration, request.getAnalysisTypes());
//
//        return AnalysisResponseDto.builder()
//                .detectedLanguage(language)
//                .confidence("auto-detected")
//                .results(results)
//                .durationMs(duration)
//                .lineCount(lineCount)
//                .build();
//    }
    
    public AnalysisResponseDto analyze(AnalysisRequestDto request) {
        long start = System.currentTimeMillis();
        String code = request.getCode();
        int lineCount = code.split("\n", -1).length;

        String language = (request.getLanguage() != null && !request.getLanguage().isBlank())
                ? request.getLanguage()
                : detectLanguage(code);

        // Resolve model: use request's model if provided, otherwise fall back to default
        String selectedModel = (request.getModel() != null && !request.getModel().isBlank())
                ? request.getModel()
                : null; // null → generateWithModel falls back to @Value model

        Map<String, String> results = new LinkedHashMap<>();
        for (String type : request.getAnalysisTypes()) {
            log.info("Running analysis type '{}' for language '{}' with model '{}'", type, language, selectedModel);
            String prompt = buildPrompt(type, code, language);
            String result = selectedModel != null
                    ? ollamaService.generateWithModel(prompt, selectedModel)
                    : ollamaService.generate(prompt);
            results.put(type, result);
        }

        long duration = System.currentTimeMillis() - start;
        return AnalysisResponseDto.builder()
                .detectedLanguage(language)
                .confidence("auto-detected")
                .results(results)
                .durationMs(duration)
                .lineCount(lineCount)
                .build();
    }

    // Language Detection
    public String detectLanguage(String code) {
        String prompt = """
                You are a language detection expert.
                Examine the following code snippet and respond with ONLY the programming language name
                and version if identifiable (e.g. "Java 8", "Python 2.7", "COBOL", "JavaScript ES5").
                Do not add any explanation — just the language name.
 
                Code:
                ```
                %s
                ```
                """.formatted(code);
        return ollamaService.generate(prompt).trim();
    }

    // Prompt Builders
    public String buildPrompt(String type, String code, String language) {
        return switch (type.toUpperCase()) {
            case "EXPLAIN"       -> explainPrompt(code, language);
            case "LINE_BY_LINE"  -> lineByLinePrompt(code, language);
            case "BUG_DETECTION" -> bugDetectionPrompt(code, language);
            case "MODERNIZATION" -> modernizationPrompt(code, language);
            case "REFACTORING"   -> refactoringPrompt(code, language);
            case "DOCUMENTATION" -> documentationPrompt(code, language);
            case "EXECUTION_FLOW"-> executionFlowPrompt(code, language);
            default              -> genericPrompt(type, code, language);
        };
    }

    public String explainPrompt(String code, String lang) {
        return """
                You are a senior software engineer and technical writer.
                Explain the following %s code in clear, plain English suitable for both technical and non-technical readers.
                Structure your response with:
                1. **Overview** – What does this code do at a high level?
                2. **Key Components** – Main classes, functions, or sections.
                3. **Business Logic** – What real-world problem does it solve?
                4. **Concerns / Risks** – Any obvious issues, outdated patterns, or risks.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String lineByLinePrompt(String code, String lang) {
        return """
                You are a code review expert. Perform a detailed line-by-line explanation of the following %s code.
                For each significant line or block, explain:
                - What it does
                - Why it might be problematic (if applicable), flagged with ⚠️ or 🐛
                - Modern alternatives if relevant
 
                Format your response as a structured list with line numbers where possible.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String bugDetectionPrompt(String code, String lang) {
        return """
                You are a security and code quality expert specializing in %s.
                Analyze the following code and identify ALL bugs, security vulnerabilities, logic errors,
                deprecated API usage, and anti-patterns.
 
                For each issue found, provide:
                - **Severity**: CRITICAL / HIGH / MEDIUM / LOW
                - **Location**: Line number or code reference
                - **Description**: What is wrong and why
                - **Buggy Code**: The problematic snippet
                - **Fix**: The corrected code
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String modernizationPrompt(String code, String lang) {
        return """
                You are a modernization architect specializing in legacy %s systems.
                Analyze this code and provide a complete modernization roadmap covering:
 
                1. **Current State Assessment** – Legacy patterns, tech debt score, risks.
                2. **Target Architecture** – Recommended modern frameworks, language version, patterns.
                3. **Migration Steps** – Step-by-step migration plan with effort estimates.
                4. **Code Rewrites** – Show before/after code examples for key sections.
                5. **Cloud & DevOps Readiness** – Containerization, testing, CI/CD recommendations.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String refactoringPrompt(String code, String lang) {
        return """
                You are a clean code expert and refactoring specialist for %s.
                Review this code and provide comprehensive refactoring suggestions:
 
                1. **Design Pattern Improvements** – Which patterns to apply and why.
                2. **SOLID Principle Violations** – What is violated and how to fix it.
                3. **Performance Optimizations** – Memory, CPU, I/O improvements.
                4. **Code Smells** – Long methods, duplication, magic numbers, etc.
                5. **Refactored Code** – Provide the improved version of key sections.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String documentationPrompt(String code, String lang) {
        return """
                You are a technical documentation expert for %s.
                Generate complete documentation for the following code including:
 
                1. **Javadoc / JSDoc / Docstrings** – For every public class, method, and parameter.
                2. **README Section** – A module-level README excerpt.
                3. **API Documentation** – If this is an API or service.
                4. **Usage Examples** – How to use this code correctly.
 
                Return the fully documented code with all comments added.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String executionFlowPrompt(String code, String lang) {
        return """
                You are a code analysis expert specializing in %s execution flows.
                Trace and document the complete execution flow of this code:
 
                1. **Entry Points** – Where execution begins.
                2. **Call Graph** – Method/function call hierarchy.
                3. **Data Flow** – How data transforms as it moves through the code.
                4. **Control Flow** – Branches, loops, and conditions.
                5. **Exit Points** – Return values, side effects, exceptions.
 
                Use ASCII diagrams or structured text to illustrate the flow.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(lang, lang.toLowerCase(), code);
    }

    public String genericPrompt(String type, String code, String lang) {
        return """
                Perform a "%s" analysis on the following %s code and provide detailed, actionable insights.
 
                Code:
                ```%s
                %s
                ```
                """.formatted(type, lang, lang.toLowerCase(), code);
    }

    // Supported Analysis Types (for frontend validation_
    public List<String> getSupportedAnalysisTypes() {
        return List.of(
                "EXPLAIN",
                "LINE_BY_LINE",
                "BUG_DETECTION",
                "MODERNIZATION",
                "REFACTORING",
                "DOCUMENTATION",
                "EXECUTION_FLOW"
        );
    }
}
