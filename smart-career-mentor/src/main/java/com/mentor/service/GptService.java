package com.mentor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class GptService {

    // Better practice: Use @Value for configuration
    private final String apiKey;
    private final WebClient webClient;

    public GptService() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // Optional: don't crash if file is missing
            .load();

        this.apiKey = dotenv.get("OPENAI_API_KEY");

        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("OpenAI API key not found in environment variables");
        }

        // Build the post request
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + this.apiKey)
                .defaultHeader("Content-type", "application/json")
                .build();
    }

    public String analyzeResumewithGPT(String resume, String job){
        Map<String, Object> body = new HashMap<>();

        body.put("model", "gpt-3.5-turbo");

        //Building message for request
        List<Map<String, String>> messages = new ArrayList<>(); 

        // System message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful resume assistant. Analyze the resume against the job description and provide feedback.");
        messages.add(systemMessage);

        // User message
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Here is a resume: " + resume + " and a jobdescription: " + job);
        messages.add(userMessage);

        body.put("messages", messages);

        try {
            // Sending the POST request to OpenAI API and handling the response
            String response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // Block here for simplicity, or use async with Mono

            return response;  // Return the GPT API response

        } catch (WebClientResponseException e) {
            // Handling specific WebClient response exceptions (HTTP errors)
            return String.format("""
                    Error from OpenAI API:
                    Status: %d
                    Response Body: %s
                    Headers: %s
                    """, 
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    e.getHeaders());

        } catch (Exception e) {
            // Catching any other exceptions (e.g., network failure, unexpected issues)
            return "Unexpected error: " + e.getMessage() + "\nStack Trace: " + e.getStackTrace()[0];
        }
    }
}
