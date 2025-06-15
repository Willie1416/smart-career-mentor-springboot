package com.mentor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

@Service
public class GptService {

    private final WebClient webClient;
    private final String ollamaUrl;
    private final String modelName;

    public GptService(
            @Value("${ollama.url:http://localhost:11434}") String ollamaUrl,
            @Value("${ollama.model:deepseek-coder:instruct}") String modelName) {
        this.ollamaUrl = ollamaUrl;
        this.modelName = modelName;
        this.webClient = WebClient.builder()
                .baseUrl(ollamaUrl + "/api/generate")
                .defaultHeader("Content-type", "application/json")
                .build();
    }

    public String analyzeResumewithGPT(String resume, String job) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("prompt", buildPrompt(resume, job));
        body.put("stream", false);

        try {
            String response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;

        } catch (WebClientResponseException e) {
            return String.format("""
                    Error from Ollama:
                    Status: %d
                    Response Body: %s
                    Headers: %s
                    """, 
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    e.getHeaders());

        } catch (Exception e) {
            return "Error: Could not connect to Ollama service. Please make sure Ollama is running locally at " + ollamaUrl;
        }
    }

    private String buildPrompt(String resume, String jobDescription) {
        return """
                You are a helpful assistant evaluating resumes.
                Analyze the following resume and job description, then give constructive feedback on how well they align,
                and what could be improved.

                Resume:
                %s

                Job Description:
                %s

                Please provide feedback in the following format:
                1. Overall Match: [Brief assessment of how well the resume matches the job]
                2. Strengths: [List key strengths that align with the job]
                3. Areas for Improvement: [List specific areas that could be enhanced]
                4. Recommendations: [Specific suggestions for improvement]

                Feedback:
                """.formatted(resume, jobDescription);
    }
} 