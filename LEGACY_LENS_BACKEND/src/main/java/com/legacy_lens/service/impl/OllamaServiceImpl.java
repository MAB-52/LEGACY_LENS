//package com.legacy_lens.service.impl;
//
//import com.legacy_lens.service.OllamaService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class OllamaServiceImpl implements OllamaService {
//
//    @Value("${ollama.base-url}")
//    private String ollamaBaseUrl;
//
//    @Value("${ollama.model}")
//    private String model;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * Send a prompt to Ollama and return the complete response text.
//     *
//     * @param prompt  The full prompt string to send
//     * @return        Model's response as a plain string
//     */
//    public String generate(String prompt) {
//        String url = ollamaBaseUrl + "/api/generate";
//
//        Map<String, Object> body = Map.of(
//                "model", model,
//                "prompt", prompt,
//                "stream", false   // wait for the full response
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        try {
//            String jsonBody = objectMapper.writeValueAsString(body);
//            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//
//            log.debug("Sending prompt to Ollama model '{}', prompt length={}", model, prompt.length());
//            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode root = objectMapper.readTree(response.getBody());
//                String result = root.path("response").asText();
//                log.debug("Ollama responded with {} chars", result.length());
//                return result;
//            }
//
//            log.warn("Ollama returned non-2xx status: {}", response.getStatusCode());
//            return "Ollama returned an unexpected status: " + response.getStatusCode();
//
//        } catch (Exception e) {
//            log.error("Error communicating with Ollama at {}: {}", url, e.getMessage());
//            return "Error: Could not reach Ollama. Make sure it is running on " + ollamaBaseUrl
//                    + " and model '" + model + "' is pulled.";
//        }
//    }
//
//    @Override
//    public List<String> getAvailableModels() {
//        String url = ollamaBaseUrl + "/api/tags";
//        try {
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode root = objectMapper.readTree(response.getBody());
//                JsonNode models = root.path("models");
//                List<String> names = new ArrayList<>();
//                models.forEach(m -> names.add(m.path("name").asText()));
//                log.info("Available Ollama models: {}", names);
//                return names;
//            }
//        } catch (Exception e) {
//            log.error("Could not fetch Ollama models: {}", e.getMessage());
//        }
//        return List.of();
//    }
//    
//    @Override
//    public String generateWithModel(String prompt, String modelName) {
//        String url = ollamaBaseUrl + "/api/generate";
//        Map<String, Object> body = Map.of(
//                "model", modelName,   // use the passed-in model
//                "prompt", prompt,
//                "stream", false
//        );
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        try {
//            String jsonBody = objectMapper.writeValueAsString(body);
//            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//            log.debug("Sending prompt to Ollama model '{}', prompt length={}", modelName, prompt.length());
//            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode root = objectMapper.readTree(response.getBody());
//                return root.path("response").asText();
//            }
//            return "Ollama returned an unexpected status: " + response.getStatusCode();
//        } catch (Exception e) {
//            log.error("Error communicating with Ollama: {}", e.getMessage());
//            return "Error: Could not reach Ollama at " + ollamaBaseUrl;
//        }
//    }
//
//}

package com.legacy_lens.service.impl;

import com.legacy_lens.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {

	@Value("${ollama.base-url}")
	private String ollamaBaseUrl;

	@Value("${ollama.model}")
	private String defaultModel;

	private final RestTemplate restTemplate = new RestTemplate();

	// ── generate with default model (existing behaviour) ─────────
	@Override
	public String generate(String prompt) {
		return generateWithModel(prompt, defaultModel);
	}

	// ── generate with a specific model ───────────────────────────
	@Override
	public String generateWithModel(String prompt, String modelName) {
		String url = ollamaBaseUrl + "/api/generate";

		Map<String, Object> body = Map.of("model", modelName, "prompt", prompt, "stream", false);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		try {
			log.debug("Sending prompt to Ollama model '{}', length={}", modelName, prompt.length());

			// Let Spring deserialise directly into a Map — no ObjectMapper needed
			@SuppressWarnings("unchecked")
			ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Object result = response.getBody().get("response");
				String text = result != null ? result.toString() : "";
				log.debug("Ollama responded with {} chars", text.length());
				return text;
			}

			log.warn("Ollama returned non-2xx: {}", response.getStatusCode());
			return "Ollama returned an unexpected status: " + response.getStatusCode();

		} catch (Exception e) {
			log.error("Error communicating with Ollama at {}: {}", url, e.getMessage());
			return "Error: Could not reach Ollama. Make sure it is running on " + ollamaBaseUrl + " and model '"
					+ modelName + "' is pulled.";
		}
	}

	// ── fetch all locally installed models ────────────────────────
	@Override
	public List<String> getAvailableModels() {
		String url = ollamaBaseUrl + "/api/tags";

		try {
			log.info("Fetching available Ollama models from {}", url);

			@SuppressWarnings("unchecked")
			ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

				Object raw = response.getBody().get("models");
				if (raw instanceof List<?> modelList) {
					List<String> names = new ArrayList<>();
					for (Object item : modelList) {
						if (item instanceof Map<?, ?> modelMap) {
							Object name = modelMap.get("name");
							if (name != null)
								names.add(name.toString());
						}
					}
					log.info("Found {} model(s): {}", names.size(), names);
					return names;
				}
			}

			log.warn("Ollama /api/tags returned status: {}", response.getStatusCode());

		} catch (Exception e) {
			log.error("Could not fetch Ollama models from {}: {}", url, e.getMessage());
		}

		return List.of();
	}
}