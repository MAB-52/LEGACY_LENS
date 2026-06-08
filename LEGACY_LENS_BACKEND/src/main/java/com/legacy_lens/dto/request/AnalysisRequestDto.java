package com.legacy_lens.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisRequestDto {

    @NotBlank(message = "Code must not be blank")
    private String code;

    // Optional hint; if null the backend auto-detects via Ollama
    private String language;


    //Analysis types requested.
    //Valid values: EXPLAIN, LINE_BY_LINE, BUG_DETECTION, MODERNIZATION,
    //REFACTORING, DOCUMENTATION, EXECUTION_FLOW

    @NotEmpty(message = "At least one analysis type is required")
    private List<String> analysisTypes;
    
    private String model; 
}