package com.legacy_lens.service;

import java.util.List;

public interface OllamaService {

    String generate(String prompt);
    
    String generateWithModel(String prompt, String model);
    
    List<String> getAvailableModels();
}
