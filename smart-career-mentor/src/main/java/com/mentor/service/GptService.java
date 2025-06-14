package com.mentor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GptService {

String apiKey = System.getenv("openai.api.key"); // add ;


    // Build the post request
    WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-type", "application/json")
            .build();


    public String analyzeResumewithGPT(String resume, String job){
        Map<String, Object> body = new HashMap<>();

        body.put("model", "gpt-4");

        //Building message for request
        List<Map<String, String>> messages = new ArrayList<>(); 

        // System message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful resume assitant.");
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
            return "Error from OpenAI API: " + e.getStatusCode() + " - " + e.getMessage();

        } catch (Exception e) {
            // Catching any other exceptions (e.g., network failure, unexpected issues)
            return "Error processing request: " + e.getMessage();
        }
    }

}
